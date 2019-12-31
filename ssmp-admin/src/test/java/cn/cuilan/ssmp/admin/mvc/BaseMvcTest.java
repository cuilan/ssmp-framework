package cn.cuilan.ssmp.admin.mvc;

import cn.cuilan.ssmp.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matcher;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
@Transactional
public abstract class BaseMvcTest {

    ObjectMapper mapper = new ObjectMapper();

    @Resource
    private MockMvc mockMvc;

    /**
     * get请求
     *
     * @param uri get请求uri地址
     */
    protected Mock getMock(String uri) {
        try {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            Mock mock = new Mock(uri);
            mock.requestBuilder = builder;
            return mock;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * post请求
     *
     * @param uri post请求uri地址
     */
    protected Mock postMock(String uri) {
        try {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            Mock mock = new Mock(uri);
            mock.requestBuilder = builder;
            return mock;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    interface BuildAppender {
        void append(MockHttpServletRequestBuilder builder);
    }

    class Mock {
        private String uri;
        private String bodyJson;
        private MockHttpServletRequestBuilder requestBuilder;

        public Mock(String uri) {
            this.uri = uri;
        }

        public Mock token(String token) {
            requestBuilder.cookie(new Cookie(Constants.TEST_ADMIN_COOKIE_NAME, token));
            return this;
        }

        public Mock param(String name, Number value) {
            requestBuilder.param(name, value.toString());
            return this;
        }

        public Mock param(String name, String... value) {
            requestBuilder.param(name, value);
            return this;
        }

        public Mock bodyBean(Object requestBodyBean) {
            try {
                bodyJson = mapper.writer().writeValueAsString(requestBodyBean);
                requestBuilder.content(bodyJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Mock appendRequest(BuildAppender buildAppender) {
            buildAppender.append(requestBuilder);
            return this;
        }

        public MockResult execute() {
            try {
                log.info("测试接口: {}", uri);
                ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
                String resp = resultActions.andReturn().getResponse().getContentAsString();
                log.info("返回响应: {}", resp);
                return new MockResult(resultActions, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public class MockResult implements ResultActions {

            private ResultActions resultActions;

            private String resp;

            public MockResult(ResultActions resultActions, String resp) {
                this.resultActions = resultActions;
                this.resp = resp;
            }

            /**
             * 预期返回消息
             *
             * @param message 预期返回消息
             */
            public <T> MockResult andExpectMessage(String message) {
                andExpectJsonPath("$.message", message);
                return this;
            }

            /**
             * 预期返回状态码
             *
             * @param code 预期返回状态码
             */
            public <T> MockResult andExpectCode(int code) {
                andExpectJsonPath("$.code", code);
                return this;
            }

            public <T> MockResult andExpectJsonPath(String expression, Object expectValue) {
                try {
                    resultActions.andExpect(MockMvcResultMatchers.jsonPath(expression).value(expectValue));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return this;
            }

            public <T> MockResult andExpectJsonPath(String expression, Matcher<T> matcher) {
                try {
                    resultActions.andExpect(MockMvcResultMatchers.jsonPath(expression, matcher));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return this;
            }

            @Override
            public MockResult andExpect(ResultMatcher resultMatcher) {
                try {
                    resultActions.andExpect(resultMatcher);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return this;
            }

            @Override
            public MockResult andDo(ResultHandler resultHandler) throws Exception {
                resultActions.andDo(resultHandler);
                return this;
            }

            @Override
            public MvcResult andReturn() {
                return resultActions.andReturn();
            }

            public <T> T andReturnJsonPath(String jsonPath) {
                return JsonPath.read(resp, jsonPath);
            }
        }
    }
}
