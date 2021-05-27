package com.tmb.oneapp.productsexpservice.model.request.loan;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ProductRequest {
    @NotEmpty
    private String product;
}
