/**
 * Copyright (c) 2018--2020, Saalfeld lab
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.janelia.saalfeldlab.n5.metadata;

import org.janelia.saalfeldlab.n5.DatasetAttributes;

import com.google.gson.GsonBuilder;

import net.imglib2.realtransform.AffineTransform3D;

/**
 * Marker interface for single-scale or multi-scale N5 metadata (and possibly more).
 */
public interface N5Metadata {

    static GsonBuilder getGsonBuilder()
    {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        registerGsonTypeAdapters( gsonBuilder );
        return gsonBuilder;
    }

    static void registerGsonTypeAdapters( final GsonBuilder gsonBuilder )
    {
        gsonBuilder.registerTypeAdapter( AffineTransform3D.class, new AffineTransform3DJsonAdapter() );
    }

    public String getPath();

    /**
     * Return the dataset attributes if this metadata object represents a single dataset.
     * Can return null if this metadata object represents multiscale set of datasets, for example.
     *
     * @return the attributes
     */
    public DatasetAttributes getAttributes();
    
    /**
     * Returns true if this path corresponds to a dataset.
     * 
     * @return is this a dataset
     */
    public default boolean isDataset()
    {
    	return getAttributes() != null;
    }
}
