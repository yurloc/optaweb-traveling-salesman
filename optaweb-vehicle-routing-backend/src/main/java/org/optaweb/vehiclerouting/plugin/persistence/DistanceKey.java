/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.persistence;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

/**
 * Composite key for {@link DistanceEntity}.
 */
@Embeddable
class DistanceKey implements Serializable {

    // TODO make it a foreign key to LocationEntity
    private Long fromId;
    private Long toId;

    private DistanceKey() {
        // for JPA
    }

    DistanceKey(long fromId, long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    Long getFromId() {
        return fromId;
    }

    Long getToId() {
        return toId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistanceKey that = (DistanceKey) o;
        return fromId.equals(that.fromId) &&
                toId.equals(that.toId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, toId);
    }
}
