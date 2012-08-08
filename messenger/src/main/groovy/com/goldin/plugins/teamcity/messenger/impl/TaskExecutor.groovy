package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import java.util.concurrent.Semaphore
import org.gcontracts.annotations.Invariant
import org.gcontracts.annotations.Requires


/**
 * Executor-like invocation of the task specified.
 */
@Invariant({ this.taskRunnable })
class TaskExecutor
{
    private final TaskRunnable taskRunnable


    /**
     * {@link Runnable} class for the instance background thread is created with.
     * When the thread runs, it blocks on semaphore waiting for it to be released, then performs the task.
     */
    @Invariant({ this.semaphore && this.task && this.context })
    class TaskRunnable implements Runnable
    {
        private final Semaphore       semaphore = new Semaphore( 0 )
        private final Closure         task
        private final MessagesContext context


        @Requires({ task && context })
        TaskRunnable ( Closure task, MessagesContext context )
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
    TaskExecutor ( Closure task, MessagesContext context )
    {
        taskRunnable = new TaskRunnable( task, context )
        Thread t     = new Thread( taskRunnable )
        t.daemon     = true
        t.name       = 'Messenger Plugin persistency background thread'
        t.start()
    }


    /**
     * Executes the task.
     * @param async whether task should be executed in caller's or background thread.
     *              If <code>true</code> (default) task is executed asynchronously in background thread.
     *              Otherwise task is executed in caller's thread.
     */
    void execute( boolean async = true ) { taskRunnable.execute( async ) }
}
