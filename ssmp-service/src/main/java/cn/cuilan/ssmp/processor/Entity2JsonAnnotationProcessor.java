package cn.cuilan.ssmp.processor;

import cn.cuilan.ssmp.annotation.Entity2Json;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author zhang.yan
 * @date 2020/8/21
 */
@AutoService(Process.class)
public class Entity2JsonAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> entity2JsonList = roundEnv.getElementsAnnotatedWith(Entity2Json.class);
        for (Element element : entity2JsonList) {
            // TODO
        }

        return false;
    }
}
