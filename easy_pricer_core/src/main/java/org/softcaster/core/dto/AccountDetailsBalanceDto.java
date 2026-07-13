/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

import java.math.BigDecimal;

public record AccountDetailsBalanceDto(
    Integer accountId,
    String code,
    String description,
    BigDecimal totalDebit,
    BigDecimal totalCredit
) {}
