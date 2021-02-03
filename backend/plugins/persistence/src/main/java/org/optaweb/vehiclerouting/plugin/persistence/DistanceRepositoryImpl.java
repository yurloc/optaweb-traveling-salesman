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

import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.distance.DistanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class DistanceRepositoryImpl implements DistanceRepository {

    private final DistanceCrudRepository distanceRepository;

    @Autowired
    DistanceRepositoryImpl(DistanceCrudRepository distanceRepository) {
        this.distanceRepository = distanceRepository;
    }

    @Override
    public void saveDistance(Location from, Location to, long distance) {
        DistanceEntity distanceEntity = new DistanceEntity(new DistanceKey(from.id(), to.id()), distance);
        distanceRepository.save(distanceEntity);
    }

    @Override
    public long getDistance(Location from, Location to) {
        Optional<DistanceEntity> optional = distanceRepository.findById(new DistanceKey(from.id(), to.id()));
        if (optional.isPresent()) {
            return optional.get().getDistance();
        }
        return -1;
    }

    @Override
    public void deleteDistances(Location location) {
        distanceRepository.deleteByFromIdOrToId(location.id());
    }

    @Override
    public void deleteAll() {
        distanceRepository.deleteAll();
    }
}