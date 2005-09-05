/* $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.proxy.interceptor;

import org.aopalliance.intercept.MethodInvocation;

import java.util.concurrent.Executor;

/**
 * A method interceptor that uses an {@link Executor} to execute the method invocation.
 * <p />
 * <b>Note</b>: Only <em>void</em> methods can be intercepted using this class!  Any attempts to intercept
 * non-void methods will result in an {@link IllegalArgumentException}.  If the proxy interfaces include non-void
 * methods, try using a {@link FilteredMethodInterceptor} along with a {@link org.apache.commons.proxy.interceptor.filter.ReturnTypeFilter} to wrap an instance of this class.
 * @author James Carman
 * @version 1.0
 */
public class ExecutorMethodInterceptor extends AbstractMethodInterceptor
{
    private final Executor executor;

    public ExecutorMethodInterceptor( Executor executor )
    {
        this.executor = executor;
    }

    public Object invoke( final MethodInvocation methodInvocation ) throws Throwable
    {
        if( Void.TYPE.equals( methodInvocation.getMethod().getReturnType() ) )
        {
            executor.execute( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        methodInvocation.proceed();
                    }
                    catch( Throwable t )
                    {
                        getLog().error( "Method invocation threw an exception.", t );
                    }
                }
            } );
            return null;
        }
        else
        {
            throw new IllegalArgumentException( "Only void methods can be executed in a different thread." );
        }
    }
}
