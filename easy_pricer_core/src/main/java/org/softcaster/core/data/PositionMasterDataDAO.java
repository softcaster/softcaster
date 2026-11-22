package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.softcaster.core.dto.PositionMasterDataDto;
import org.softcaster.engine.enums.PositionType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gerarchia delle posizioni: i nodi non postable (legal entity, desk,
 * portafogli, gruppi) aggregano, solo le POSITION alimentano position_detail.
 */
@Service("positionMasterDataDAO")
public class PositionMasterDataDAO {

    @Resource
    private PositionMasterDataRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public PositionMasterData findByIdPosition(Integer idPosition) {
        return repository.findByIdPosition(idPosition);
    }

    @Transactional(readOnly = true)
    public PositionMasterData findByCode(String code) {
        return repository.findByCode(code);
    }

    /**
     * Solo le posizioni postable: e' l'insieme su cui girano job, deal capture e valorizzazione.
     */
    @Transactional(readOnly = true)
    public List<PositionMasterData> findAll() {
        return repository.findByPostable(true, sortByCode);
    }

    /**
     * Nodi aggregatori della gerarchia (non postable).
     */
    @Transactional(readOnly = true)
    public List<PositionMasterData> findHierarchyNodes() {
        return repository.findByPostable(false, sortByCode);
    }

    /**
     * Tutti i nodi, postable e non.
     */
    @Transactional(readOnly = true)
    public List<PositionMasterData> findAllNodes() {
        return repository.findAll(sortByCode);
    }

    public List<PositionMasterDataDto> findAllDto() {
        List<PositionMasterData> list = findAll();
        List<PositionMasterDataDto> dtoList = null;

        if (list != null && !list.isEmpty()) {
            dtoList = new ArrayList<>();
            PositionMasterDataDto dto;
            for (PositionMasterData pmd : list) {
                dto = new PositionMasterDataDto();
                dto.setGenericMasterDataId(pmd.getIdPosition());
                dto.setCode(pmd.getCode());
                dto.setDescription(pmd.getDescription());
                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    @Transactional
    public PositionMasterData saveOrUpdate(PositionMasterData positionMasterData) {
        validateHierarchy(positionMasterData);
        return repository.save(positionMasterData);
    }

    @Transactional
    public void delete(PositionMasterData positionMasterData) {
        if (positionMasterData.getIdPosition() != null
                && repository.existsByParentIdPosition(positionMasterData.getIdPosition())) {
            throw new IllegalStateException("Node " + positionMasterData.getCode() + " has children and cannot be deleted");
        }
        repository.delete(positionMasterData);
    }

    /**
     * Regole della gerarchia: tipo coerente con is_postable, padre non postable,
     * niente cicli, rango del figlio maggiore di quello del padre (GROUP escluso),
     * niente figli sotto una posizione e niente cambio di postable con dettagli esistenti.
     */
    private void validateHierarchy(PositionMasterData node) {
        PositionType type = node.getPositionType();
        if (type == null) {
            throw new IllegalArgumentException("Position type is required");
        }
        if (node.isPostable() != type.isPostable()) {
            throw new IllegalArgumentException("Only nodes of type " + PositionType.POSITION.getDescription() + " can be postable");
        }

        Integer id = node.getIdPosition();
        if (id != null) {
            if (!node.isPostable() && repository.hasDetails(id)) {
                throw new IllegalStateException("Node " + node.getCode() + " has position details and must stay postable");
            }
            if (node.isPostable() && repository.existsByParentIdPosition(id)) {
                throw new IllegalStateException("Node " + node.getCode() + " has children and cannot be postable");
            }
        }

        PositionMasterData parent = node.getParent();
        if (parent == null) {
            return;
        }
        // il padre va riletto: puo' arrivare dal client come solo id
        PositionMasterData current = parent.getIdPosition() == null ? null : repository.findByIdPosition(parent.getIdPosition());
        if (current == null) {
            throw new IllegalArgumentException("Parent node not found");
        }
        if (current.isPostable()) {
            throw new IllegalArgumentException("Parent " + current.getCode() + " is a position and cannot have children");
        }

        PositionType rankingAncestorType = null;
        Set<Integer> visited = new HashSet<>();
        while (current != null) {
            if (current.getIdPosition().equals(id) || !visited.add(current.getIdPosition())) {
                throw new IllegalArgumentException("Parent " + parent.getCode() + " would create a cycle");
            }
            if (rankingAncestorType == null && current.getPositionType() != PositionType.GROUP) {
                rankingAncestorType = current.getPositionType();
            }
            current = current.getParent();
        }
        if (!type.canBeChildOf(rankingAncestorType)) {
            throw new IllegalArgumentException(type.getDescription() + " cannot be placed under " + rankingAncestorType.getDescription());
        }
    }
}
