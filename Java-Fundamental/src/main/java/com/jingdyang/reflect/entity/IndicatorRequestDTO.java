package com.jingdyang.reflect.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class IndicatorRequestDTO {
    @Length(max = 3, message = "一次请求理财产品数量不应超过3")
    private List<String> wmpIds;
}
