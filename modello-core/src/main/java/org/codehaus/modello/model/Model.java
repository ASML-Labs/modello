package org.codehaus.modello.model;

/*
 * Copyright (c) 2004, Jason van Zyl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.modello.ModelloRuntimeException;

/**
 * @author <a href="mailto:jason@modello.org">Jason van Zyl</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 *
 * @version $Id$
 */
public class Model
    extends BaseElement
{
    private String id;

    private String packageName;

    private String root;

    private List classes = new ArrayList();

    private List defaults = new ArrayList();

    private List interfaces = new ArrayList();

    private transient Map classMap = new HashMap();

    private transient Map defaultMap = new HashMap();

    private transient Map interfaceMap = new HashMap();

    public Model()
    {
        super( true );
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getRoot()
    {
        return root;
    }

    public void setRoot( String root )
    {
        this.root = root;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName( String packageName )
    {
        this.packageName = packageName;
    }

    public List getAllClasses()
    {
        return classes;
    }

    public List getClasses( Version version )
    {
        ArrayList classList = new ArrayList();

        for (Iterator i = classes.iterator(); i.hasNext(); )
        {
            ModelClass currentClass = (ModelClass) i.next();

            if ( version.inside( currentClass.getVersionRange() ) )
            {
                classList.add( currentClass );
            }
        }

        return classList;
    }

    public ModelClass getClass( String type, Version version )
    {
        return getClass( type, new VersionRange( version.getMajor() + "." + version.getMinor() + "." + version.getMicro() ) );
    }

    public ModelClass getClass( String type, VersionRange versionRange )
    {
        ArrayList classList = (ArrayList) classMap.get( type );
        
        if ( classList != null )
        {
            for (Iterator i = classList.iterator(); i.hasNext(); )
            {
                ModelClass modelClass = (ModelClass) i.next();

                if (  versionRange.getFromVersion().inside( modelClass.getVersionRange() )
                    && versionRange.getToVersion().inside( modelClass.getVersionRange() ) )
                {
                    return modelClass;
                }
            }
        }

        throw new ModelloRuntimeException( "There are no class '" + type + "' in version range '" + versionRange.toString() + "'." );
    }

    public void addClass( ModelClass modelClass )
    {
        if ( classMap.containsKey( modelClass.getName() ) )
        {
            ArrayList classList = (ArrayList) classMap.get( modelClass.getName() );

            for (Iterator i = classList.iterator(); i.hasNext(); )
            {
                ModelClass currentClass = (ModelClass) i.next();

                if ( VersionUtil.isInConflict( modelClass.getVersionRange(), currentClass.getVersionRange() ) )
                {
                    throw new ModelloRuntimeException( "Duplicate class: " + modelClass.getName() + "." );
                }
            }
        }
        else
        {
            ArrayList classList = new ArrayList();
        
            classMap.put( modelClass.getName(), classList );
        }

        getAllClasses().add( modelClass );

        ( (ArrayList) classMap.get( modelClass.getName() ) ).add( modelClass );
    }

    public List getDefaults()
    {
        return defaults;
    }

    public ModelDefault getDefault( String key )
        throws ModelValidationException
    {
        ModelDefault modelDefault = (ModelDefault) defaultMap.get( key );

        if ( modelDefault == null )
        {
            modelDefault = ModelDefault.getDefault( key );
        }

        return modelDefault;
    }

    public void addDefault( ModelDefault modelDefault )
    {
        if ( defaultMap.containsKey( modelDefault.getKey() ) )
        {
            throw new ModelloRuntimeException( "Duplicate default: " + modelDefault.getKey() + "." );
        }

        getDefaults().add( modelDefault );

        defaultMap.put( modelDefault.getKey(), modelDefault );
    }

    public List getAllInterfaces()
    {
        return interfaces;
    }

    public List getInterfaces( Version version )
    {
        ArrayList interfaceList = new ArrayList();

        for (Iterator i = interfaces.iterator(); i.hasNext(); )
        {
            ModelInterface currentInterface = (ModelInterface) i.next();

            if ( version.inside( currentInterface.getVersionRange() ) )
            {
                interfaceList.add( currentInterface );
            }
        }

        return interfaceList;
    }

    public ModelInterface getInterface( String type, Version version )
    {
        return getInterface( type, new VersionRange( version.getMajor() + "." + version.getMinor() + "." + version.getMicro() ) );
    }

    public ModelInterface getInterface( String type, VersionRange versionRange )
    {
        ArrayList interfaceList = (ArrayList) interfaceMap.get( type );
        
        if ( interfaceList != null )
        {
            for (Iterator i = interfaceList.iterator(); i.hasNext(); )
            {
                ModelInterface modelInterface = (ModelInterface) i.next();

                if (  versionRange.getFromVersion().inside( modelInterface.getVersionRange() )
                    && versionRange.getToVersion().inside( modelInterface.getVersionRange() ) )
                {
                    return modelInterface;
                }
            }
        }

        throw new ModelloRuntimeException( "There are no interface '" + type + "' in version range '" + versionRange.toString() + "'." );
    }

    public void addInterface( ModelInterface modelInterface )
    {
        if ( interfaceMap.containsKey( modelInterface.getName() ) )
        {
            ArrayList interfaceList = (ArrayList) interfaceMap.get( modelInterface.getName() );

            for (Iterator i = interfaceList.iterator(); i.hasNext(); )
            {
                ModelInterface currentInterface = (ModelInterface) i.next();

                if ( VersionUtil.isInConflict( modelInterface.getVersionRange(), currentInterface.getVersionRange() ) )
                {
                    throw new ModelloRuntimeException( "Duplicate interface: " + modelInterface.getName() + "." );
                }
            }
        }
        else
        {
            ArrayList interfaceList = new ArrayList();
        
            interfaceMap.put( modelInterface.getName(), interfaceList );
        }

        getAllInterfaces().add( modelInterface );

        ( (ArrayList) interfaceMap.get( modelInterface.getName() ) ).add( modelInterface );
    }

    public void initialize()
    {
        for ( Iterator i = classes.iterator(); i.hasNext(); )
        {
            ModelClass modelClass = (ModelClass) i.next();

            modelClass.initialize( this );
        }

        for ( Iterator i = interfaces.iterator(); i.hasNext(); )
        {
            ModelInterface modelInterface = (ModelInterface) i.next();

            modelInterface.initialize( this );
        }
    }

    public void validateElement()
    {
    }
}
