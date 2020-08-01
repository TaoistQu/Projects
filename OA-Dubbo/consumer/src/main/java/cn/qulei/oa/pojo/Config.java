package cn.qulei.oa.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class Config {
    @Value(value = "${config.systemName}")
    private String systemName;
}
