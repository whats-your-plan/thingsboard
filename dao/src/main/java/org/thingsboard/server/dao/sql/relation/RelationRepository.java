/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.relation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.dao.model.sql.RelationCompositeKey;
import org.thingsboard.server.dao.model.sql.RelationEntity;
import org.thingsboard.server.dao.model.sql.RuleChainEntity;

import java.util.List;
import java.util.UUID;

public interface RelationRepository
        extends CrudRepository<RelationEntity, RelationCompositeKey>, JpaSpecificationExecutor<RelationEntity> {

    List<RelationEntity> findAllByFromIdAndFromTypeAndRelationTypeGroup(UUID fromId,
                                                                        String fromType,
                                                                        String relationTypeGroup);

    List<RelationEntity> findAllByFromIdAndFromTypeAndRelationTypeAndRelationTypeGroup(UUID fromId,
                                                                                       String fromType,
                                                                                       String relationType,
                                                                                       String relationTypeGroup);

    List<RelationEntity> findAllByToIdAndToTypeAndRelationTypeGroup(UUID toId,
                                                                    String toType,
                                                                    String relationTypeGroup);

    List<RelationEntity> findAllByToIdAndToTypeAndRelationTypeAndRelationTypeGroup(UUID toId,
                                                                                   String toType,
                                                                                   String relationType,
                                                                                   String relationTypeGroup);

    List<RelationEntity> findAllByFromIdAndFromType(UUID fromId,
                                                    String fromType);

    @Query("SELECT r FROM RelationEntity r WHERE " +
            "r.fromId in (SELECT id from RuleNodeEntity where ruleChainId in " +
            "(SELECT id from RuleChainEntity where tenantId = :tenantId and type = :ruleChainType ))" +
            "AND r.fromType = 'RULE_NODE' " +
            "AND r.toType = 'RULE_CHAIN' " +
            "AND r.relationTypeGroup = 'RULE_NODE'")
    List<RelationEntity> findRuleNodeToRuleChainRelations(
            @Param("tenantId") UUID tenantId,
            @Param("ruleChainType") RuleChainType ruleChainType,
            Pageable page);

    @Transactional
    <S extends RelationEntity> S save(S entity);

    @Transactional
    void deleteById(RelationCompositeKey id);

    @Transactional
    void deleteByFromIdAndFromType(UUID fromId, String fromType);

}
