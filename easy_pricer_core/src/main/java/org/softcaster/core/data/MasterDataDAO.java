/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import org.springframework.stereotype.Repository;

@Repository
public class MasterDataDAO extends AbstractMasterDataDAO<MasterData, MasterDataRepository> {

    public MasterDataDAO(MasterDataRepository repository) {
        super(repository);
    }
}