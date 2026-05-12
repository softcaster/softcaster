/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.provider.bricks;

import org.softcaster.provider.enums.OffsetType;

/**
 *
 * @author svil
 */
public record Offset(long step, OffsetType offsetType) {

}
