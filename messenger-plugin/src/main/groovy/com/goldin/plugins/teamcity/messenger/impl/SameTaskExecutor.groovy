package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import java.util.concurrent.Semaphore
import org.gcontracts.annotations.Invariant
import org.gcontracts.annotations.Requires

/**
 * Executor-like invocation of the same task
 */
@Invariant({ this.runnable })
class SameTaskExecutor
{
    private final SameTaskRunnable runnable;


    @Invariant({ this.semaphore && this.task && this.context })
    class SameTaskRunnable implements Runnable
    {
        private final Semaphore       semaphore = new Semaphore( 0 )
        private final Closure         task;
        private final MessagesContext context;


        @Requires({ task && context })
        SameTaskRunnable ( Closure task, MessagesContext context )
        {
            this.task    = task
            this.context = context
        }


        void execute ( boolean async )
        {
            if ( async ) { semaphore.release() }
            else         { task.call() }
        }


        /**
         * Daemon thread running endlessly and blocking until new semaphore permits become available.
         * It terminates when TC server shuts down.
         */
        @Override
        void run ()
        {
            while ( true )
            {
                try
                {
                    semaphore.acquireUninterruptibly()
                    semaphore.drainPermits()
                    task.call()
                }
                catch ( e ) { context.log.error( 'Failed to acquire semaphor and run task', e ) }
            }
        }
    }


    @Requires({ task && context })
    SameTaskExecutor ( Closure task, MessagesContext context )
    {
        runnable = new SameTaskRunnable( task, context )
        Thread t = new Thread( runnable )
        t.daemon = true
        t.start()
    }


    void execute( boolean async = true ) { runnable.execute( async ) }
}
