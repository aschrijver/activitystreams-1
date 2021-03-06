/**
 * Copyright 2013 OpenSocial Foundation
 * Copyright 2013 International Business Machines Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Utility library for working with Activity Streams Actions
 * Requires underscorejs.
 *
 * @author James M Snell (jasnell@us.ibm.com)
 */
package com.ibm.common.geojson;

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxGeometries;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * A GeoJSON GeometryCollection object
 * see http://geojson.org/geojson-spec.html#geometry-collection
 * @author james
 *
 */
public final class GeometryCollection 
  extends Geometry<GeometryCollection, Geometry<?,?>> {

  public static final class Builder 
    extends GeoObject.Builder<GeometryCollection, Builder> {

    private final ImmutableList.Builder<Geometry<?,?>> list =
      ImmutableList.builder();
    
    public Builder() {
      type(GeoObject.Type.GEOMETRYCOLLECTION);
    }
    
    public Builder add(Geometry<?,?> geometry, Geometry<?,?>... geometries) {
      list.add(geometry);
      if (geometries != null)
        list.add(geometries);
      return this;
    }
    
    public Builder add(Iterable<Geometry<?,?>> geometries) {
      list.addAll(geometries);
      return this;
    }
    
    @Override
    public void preGet() {
      set("geometries", list.build());
    }
    
    @Override
    public GeometryCollection doGet() {
      return new GeometryCollection(this);
    }
  }
  
  GeometryCollection(Builder builder) {
    super(builder);
  }

  public Iterable<Geometry<?,?>> geometries() {
    return this.<Iterable<Geometry<?,?>>>get(
      "geometries",
      ImmutableList.<Geometry<?,?>>of());
  }
  
  public int size() {
    return Iterables.size(geometries());
  }
  
  @SuppressWarnings("unchecked")
  public <G extends Geometry<?,?>>G get(int idx) {
    return (G)Iterables.get(geometries(),idx);
  }
  
  public Iterator<Geometry<?,?>> iterator() {
    return geometries().iterator();
  }

  @Override
  public GeometryCollection makeWithBoundingBox() {
    return new GeometryCollection.Builder()
      .from(this)
      .add(this)
      .boundingBox(calculateBoundingBoxGeometries(this))
      .get();
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<GeometryCollection,GeometryCollection.Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(GeometryCollection obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("geometries".equals(key)) {
        Iterable<Geometry<?,?>> list = (Iterable<Geometry<?, ?>>) val;
        builder.list.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected GeometryCollection.Builder builder() {
      return GeoMakers.geometryCollection();
    }
  }
}
