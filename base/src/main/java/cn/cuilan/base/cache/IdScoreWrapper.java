package cn.cuilan.base.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class IdScoreWrapper {

    private Long id;
    private String jid;
    private Double score;

    public static Map<Long, Double> toMap(List<IdScoreWrapper> list) {
        return list.stream().filter(Objects::nonNull).collect(Collectors.toMap(IdScoreWrapper::getId, IdScoreWrapper::getScore, (k, v) -> v));
    }

}
