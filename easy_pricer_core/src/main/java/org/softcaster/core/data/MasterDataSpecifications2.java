/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import org.springframework.data.jpa.domain.Specification;
import java.sql.Date;

public class MasterDataSpecifications2 {

    public static <E extends MasterData2> Specification<E> withFilters(
            String code,
            Date maturityLessEq,
            Date maturityGreatEq) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            if (code != null && !code.isBlank()) {
                predicate = cb.and(
                    predicate,
                    cb.like(
                        cb.lower(root.get("code")),
                        "%" + code.toLowerCase() + "%"
                    )
                );
            }

            if (maturityLessEq != null) {
                predicate = cb.and(
                    predicate,
                    cb.lessThanOrEqualTo(
                        root.get("maturityDate"),
                        maturityLessEq
                    )
                );
            }

            if (maturityGreatEq != null) {
                predicate = cb.and(
                    predicate,
                    cb.greaterThanOrEqualTo(
                        root.get("maturityDate"),
                        maturityGreatEq
                    )
                );
            }

            return predicate;
        };
    }
}