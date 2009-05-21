package maze.util;

import java.awt.EventQueue;

/**
 * @author Luke Last
 * @param <T> The type of the event objects to be used by the Listeners.
 */
public class ListenerSubject<T>
{
   /**
    * Zero, one, or many listeners. Can be null. Multiple listeners form a
    * linked list.
    */
   private Listener<T> listener;

   /**
    * Stores listeners that should be notified in a delayed manner.
    */
   private Listener<T> delayedListener;

   /**
    * Stores whether or not a delayed notify Runnable has been created on the
    * EventQueue but not yet executed.
    */
   private volatile boolean pendingNotify = false;

   /**
    * Notifies all registered listeners.
    * @param event The event object that should be passed to each registered
    *           Listener.
    */
   public void notifyListeners(final T event)
   {
      final Listener<T> copy = this.listener;
      if (copy != null)
      {
         copy.eventFired(event);
      }
      if (this.delayedListener != null && this.pendingNotify == false)
      {
         this.pendingNotify = true;
         EventQueue.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               pendingNotify = false;
               final Listener<T> delayedCopy = delayedListener;
               if (delayedCopy != null)
               {
                  delayedCopy.eventFired(event);
               }
            }
         });
      }
   }

   /**
    * Registers a Listener with this subject. The given Listener is not checked
    * to see if it already is registered.
    * @param listener The Listener you would like to add.
    */
   public synchronized void addListener(final Listener<T> listener)
   {
      if (listener != null)
      {
         if (this.listener == null)
         {
            this.listener = listener;
         }
         else
         {
            this.listener = new ListenerMulticaster<T>(this.listener, listener);
         }
      }
   }

   /**
    * @param listener
    */
   public synchronized void addDelayedListener(final Listener<T> listener)
   {
      if (listener != null)
      {
         if (this.delayedListener == null)
         {
            this.delayedListener = listener;
         }
         else
         {
            this.delayedListener = new ListenerMulticaster<T>(this.delayedListener, listener);
         }
      }
   }

   /**
    * Unregisters a Listener with this subject.
    * @param listener The Listener you want to remove.
    */
   public synchronized void removeListener(final Listener<T> listener)
   {
      if (this.listener != null && listener != null)
      {
         if (this.listener == listener)
         {
            this.listener = null;
         }
         else if (this.listener instanceof ListenerMulticaster)
         {
            final ListenerMulticaster<T> multicaster = (ListenerMulticaster<T>) this.listener;
            this.listener = multicaster.remove(listener);
         }
      }
   }

   public synchronized void removeAllListeners()
   {
      this.listener = null;
   }

   /**
    * A utility class used to join 2 Listeners into one Listener interface. It
    * is used to create a readonly linked list of Listeners.<br />
    * Each instance is immutable.
    * @author Luke Last
    * @param <T>
    */
   private static class ListenerMulticaster<T> implements Listener<T>
   {
      private final Listener<T> a;
      private final Listener<T> b;

      /**
       * @param a First child branch.
       * @param b Second child branch.
       */
      private ListenerMulticaster(Listener<T> a, Listener<T> b)
      {
         this.a = a;
         this.b = b;
      }

      /**
       * Removes a Listener from this linked list. Because we are immutable new
       * instances must be created when removing an object.
       * @param toRemove The Listener to be removed.
       * @return The new Listener resulting from the removal. Can return itself
       *         if nothing changed.
       */
      private Listener<T> remove(Listener<T> toRemove)
      {
         if (this.a == toRemove)
         {
            return this.b;
         }
         if (this.b == toRemove)
         {
            return this.a;
         }
         final Listener<T> a2;
         final Listener<T> b2;
         if (this.a instanceof ListenerMulticaster)
         {
            a2 = ((ListenerMulticaster<T>) this.a).remove(toRemove);
            if (this.b instanceof ListenerMulticaster)
            {
               b2 = ((ListenerMulticaster<T>) this.b).remove(toRemove);
            }
            else
            {
               b2 = this.b;
            }
         }
         else
         {
            a2 = this.a;
            if (this.b instanceof ListenerMulticaster)
            {
               b2 = ((ListenerMulticaster<T>) this.b).remove(toRemove);
            }
            else
            {
               return this;
            }
         }
         return new ListenerMulticaster<T>(a2, b2);
      }

      /**
       * Passes the event on to children.
       */
      @Override
      public void eventFired(T event)
      {
         if (this.a != null)
         {
            this.a.eventFired(event);
         }
         if (this.b != null)
         {
            this.b.eventFired(event);
         }
      }
   }
}
