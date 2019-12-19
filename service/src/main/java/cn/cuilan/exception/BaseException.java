package cn.cuilan.exception;

/**
 * 统一运行时异常
 */
public class BaseException extends RuntimeException {

    private Integer code;

    public BaseException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BaseException(String msg) {
        super(msg);
        this.code = 400;
    }

    public Integer getCode() {
        return code;
    }

}
