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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.DoubleStream;

import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.N5TreeNode;

import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale2D;
import net.imglib2.realtransform.Scale3D;

public class N5CosemMultiScaleMetadata extends MultiscaleMetadata<N5CosemMetadata> implements N5Metadata, N5GroupParser< N5CosemMultiScaleMetadata >,
	PhysicalMetadata
{

    public final String basePath;

    private static final Predicate<String> scaleLevelPredicate = Pattern.compile("^s\\d+$").asPredicate();

    public N5CosemMultiScaleMetadata( )
    {
    	super();
        this.basePath = null;
    }

    public N5CosemMultiScaleMetadata( final String basePath, final String[] paths,
    		final AffineTransform3D[] transforms,
    		final String[] units )
    {
    	super( paths, transforms, units);
        this.basePath = basePath;
    }

	@Override
	public String getPath()
	{
		return basePath;
	}

	@Override
	public DatasetAttributes getAttributes()
	{
		return null;
	}

	/**
	*
    * Called by the {@link org.janelia.saalfeldlab.n5.N5DatasetDiscoverer}
    * while discovering the N5 tree and filling the metadata for datasets or groups.
    *
    * @param node the node
    * @return the metadata
    */
	@Override
	public N5CosemMultiScaleMetadata parseMetadataGroup( final N5TreeNode node )
	{
		final Map< String, N5TreeNode > scaleLevelNodes = new HashMap<>();
		String[] units = null;
		for ( final N5TreeNode childNode : node.childrenList() )
		{
			if ( scaleLevelPredicate.test( childNode.getNodeName() ) &&
				 childNode.getMetadata() instanceof N5CosemMetadata )
			{
				scaleLevelNodes.put( childNode.getNodeName(), childNode );
				if( units == null)
					units = ((N5CosemMetadata)childNode.getMetadata()).units();
			}
		}

		if ( scaleLevelNodes.isEmpty() )
			return null;

		final List<AffineTransform3D> transforms = new ArrayList<>();
		final List<String> paths = new ArrayList<>();
		scaleLevelNodes.forEach( (k,v) -> {
			paths.add( v.getPath() );
			transforms.add( ((N5CosemMetadata)v.getMetadata() ).getTransform().toAffineTransform3d() );
		});

		return new N5CosemMultiScaleMetadata(
				node.getPath(),
				paths.toArray( new String[ 0 ] ),
				transforms.toArray( new AffineTransform3D[ 0 ] ),
				units);
	}
	
	@Override
	public AffineGet physicalTransform()
	{
		// spatial transforms are specified by the individual scales 
		return null;
	}

}
