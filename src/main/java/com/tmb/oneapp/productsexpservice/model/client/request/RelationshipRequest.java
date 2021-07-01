package com.tmb.oneapp.productsexpservice.model.client.request;

import com.tmb.oneapp.productsexpservice.model.client.CustomerClientModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RelationshipRequest extends CustomerClientModel {

}
