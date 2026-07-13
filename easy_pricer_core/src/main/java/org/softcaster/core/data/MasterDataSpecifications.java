/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import org.springframework.data.jpa.domain.Specification;
import java.sql.Date;

public class MasterDataSpecifications {

    public static Specification<MasterData> withFilters(String code, Date maturityLessEq, Date maturityGreatEq) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            // 1) Code contains stringa
            if (code != null && !code.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }

            // 2) Maturity less or equal data
            if (maturityLessEq != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("maturityDate"), maturityLessEq));
            }

            // 3) Maturity great or equal data
            if (maturityGreatEq != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("maturityDate"), maturityGreatEq));
            }

            return predicate;
        };
    }
}
