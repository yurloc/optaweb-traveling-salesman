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

/* eslint-disable class-methods-use-this,@typescript-eslint/no-unused-vars */

import { MessagePayload } from 'store/message/types';
import { LatLngWithDescription, RoutingPlan } from 'store/route/types';
import { ServerInfo } from 'store/server/types';

export default class WebSocketClient {
  readonly backendUrl: string;

  eventSource: EventSource | null;

  constructor(backendUrl: string) {
    this.backendUrl = backendUrl;
    this.eventSource = null;
  }

  connect(successCallback: () => any, errorCallback: (err: Event) => any) {
    if (this.eventSource === null) {
      this.eventSource = new EventSource(`${this.backendUrl}/events/route`);
      this.eventSource.onopen = successCallback;
      this.eventSource.onerror = errorCallback;
    }
  }

  addLocation(latLng: LatLngWithDescription) {
    fetch(`${this.backendUrl}/location`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(latLng),
    });
  }

  addVehicle() {
    fetch(`${this.backendUrl}/vehicle`, { method: 'POST' });
  }

  loadDemo(name: string): void {
    fetch(`${this.backendUrl}/demo/${name}`, { method: 'POST' });
  }

  deleteLocation(locationId: number) {
    // TODO error callback
    fetch(`${this.backendUrl}/location/${locationId}`, { method: 'DELETE' });
  }

  deleteAnyVehicle() {
    fetch(`${this.backendUrl}/vehicle/deleteAny`, { method: 'POST' });
  }

  deleteVehicle(vehicleId: number) {
    fetch(`${this.backendUrl}/vehicle/${vehicleId}`, { method: 'DELETE' });
  }

  changeVehicleCapacity(vehicleId: number, capacity: number) {
    fetch(`${this.backendUrl}/vehicle/${vehicleId}/capacity`, {
      method: 'POST',
      body: JSON.stringify(capacity),
    });
  }

  clear() {
    fetch(`${this.backendUrl}/clear`, { method: 'POST' });
  }

  subscribeToServerInfo(subscriptionCallback: (serverInfo: ServerInfo) => any): void {
    fetch(`${this.backendUrl}/serverInfo`)
      .then((response) => response.json())
      .then((data) => subscriptionCallback(data));
  }

  subscribeToRoute(subscriptionCallback: (plan: RoutingPlan) => any): void {
    if (this.eventSource !== null) {
      this.eventSource.addEventListener('route', (event: MessageEvent) => {
        subscriptionCallback(JSON.parse(event.data));
      });
    }
  }

  subscribeToErrorTopic(subscriptionCallback: (errorMessage: MessagePayload) => any): void {
    if (this.eventSource !== null) {
      this.eventSource.addEventListener('errorMessage', (event: MessageEvent) => {
        subscriptionCallback(JSON.parse(event.data));
      });
    }
  }
}
