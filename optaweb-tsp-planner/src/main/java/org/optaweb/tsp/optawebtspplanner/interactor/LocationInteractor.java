/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.tsp.optawebtspplanner.interactor;

import java.util.Arrays;

import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.optaweb.tsp.optawebtspplanner.demo.Belgium;
import org.optaweb.tsp.optawebtspplanner.planner.TspPlannerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Performs location-related use cases.
 */
@Component
public class LocationInteractor {

    private static final Logger logger = LoggerFactory.getLogger(LocationInteractor.class);

    private final LocationRepository repository;
    private final TspPlannerComponent planner;
    private final DistanceMatrix distanceMatrix;

    public LocationInteractor(LocationRepository repository,
                              TspPlannerComponent planner,
                              DistanceMatrix distanceMatrix) {
        this.repository = repository;
        this.planner = planner;
        this.distanceMatrix = distanceMatrix;
    }

    public void loadDemo() {
        Arrays.stream(Belgium.values()).forEach(city -> addLocation(LatLng.valueOf(city.lat, city.lng)));
    }

    public void addLocation(LatLng latLng) {
        Location location = repository.createLocation(latLng);
        // TODO handle no route -> roll back the problem fact change
        distanceMatrix.addLocation(location);
        planner.addLocation(location, distanceMatrix);
        logger.info("Created {}", location);
    }

    public void removeLocation(long id) {
        Location location = repository.removeLocation(id);
        planner.removeLocation(location);
        logger.info("Deleted {}", location);
    }
}
