/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.freakchick.orange.domain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapParam {
  private Map<String, Object> map = new LinkedHashMap<>();

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }

  public void put(String key, Object value) {
    this.map.put(key, value);
  }

  public void clear() {
    this.map.clear();
  }

  public Map<String, Object> getParam() {
    Map<String, Object> param = new HashMap<>();
    param.put("map", this.map);
    return param;
  }
}
