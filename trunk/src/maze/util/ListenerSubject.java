package maze.util;

import java.awt.EventQueue;

/**
 * Implements the observer design pattern. A ListenerSubject can have Listeners
 * registered with it and then notify those Listeners when something happens.
 * This class can be extended or instances of it can be created to allow a class
 * to have multiple events that can be registered with.
 * @author Luke Last
 * @see Listener
 * @version 2009-05-30
 * @param <T> The type of the event objects to be used by the Listeners. Objects
 *           of this type are passed to each registered Listener when they are
 *           notified.
 */
public class ListenerSubject<T>
{
   /**
    * Zero, one, or many listeners. Can be null. Multiple listeners form a
    * linked list.
    */
   private Listener<T> listener;

   /**
    * Stores listeners that should be notified in a delayed manner. Can be null.
    */
   private Listener<T> delayedListener;

   /**
    * Stores whether or not a delayed notify Runnable has been created on the
    * EventQueue but not yet executed.
    */
   private volatile boolean pendingNotify = false;

   /**
    * This is created when needed.
    */
   private DelayedNotifier delayedNotifier;

   /**
    * Notifies all registered listeners. Normal listeners are notified
    * Immediately, delayed listeners are notified with a new task on the event
    * queue.
    * @param event The event object that should be passed to each registered
    *           Listener.
    */
   public final void notifyListeners(final T event)
   {
      final Listener<T> copy = this.listener;
      if (copy != null)
      {
         copy.eventFired(event);
      }
      if (this.delayedListener != null && this.pendingNotify == false)
      {
         this.pendingNotify = true;
         EventQueue.invokeLater(this.getDelayedNotifier(event));
      }
   }

   /**
    * This allows us to only create a new instance once and then reuse it for
    * better performance.
    * @param eventObject This event object is given to the notifier which it
    *           then passes to the listeners.
    * @return The one and only instance.
    */
   private DelayedNotifier getDelayedNotifier(T eventObject)
   {
      if (this.delayedNotifier == null)
      {
         this.delayedNotifier = new DelayedNotifier();
      }
      this.delayedNotifier.setEventObject(eventObject);
      return this.delayedNotifier;
   }

   /**
    * A <code>Runnable</code> which is invoked later on the event queue. We
    * create a special class for this and then instantiate it only once for
    * better performance.
    */
   private final class DelayedNotifier implements Runnable
   {
      private T eventObject;

      public void setEventObject(T eventObject)
      {
         this.eventObject = eventObject;
      }

      @Override
      public void run()
      {
         pendingNotify = false;
         final Listener<T> delayedCopy = delayedListener;
         if (delayedCopy != null)
         {
            delayedCopy.eventFired(this.eventObject);
         }
      }
   }

   /**
    * Registers a Listener with this subject. The given Listener is not checked
    * to see if it already is registered.
    * @param listener The Listener you would like to add.
    */
   public final synchronized void addListener(final Listener<T> listener)
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
   public final synchronized void addDelayedListener(final Listener<T> listener)
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
    * Unregisters a Listener with this subject. If the given listener is null or
    * not already registered no changes are made.
    * @param listener The Listener you want to remove.
    */
   public final synchronized void removeListener(final Listener<T> listener)
   {
      if (this.listener != null && listener != null)
      {
         if (this.listener == listener)
         {
            this.listener = null;
         }
         else if (this.listener instanceof ListenerMulticaster<?>)
         {
            final ListenerMulticaster<T> multicaster = (ListenerMulticaster<T>) this.listener;
            this.listener = multicaster.remove(listener);
         }
      }
   }

   /**
    * Removes all listeners registered with this subject.
    */
   public final synchronized void removeAllListeners()
   {
      this.listener = null;
   }

   /**
    * A utility class used to join 2 Listeners into one Listener interface. It
    * is used to create a read only linked list of Listeners.<br />
    * Each instance is immutable.
    * @author Luke Last
    * @param <T>
    */
   private final static class ListenerMulticaster<T> implements Listener<T>
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
         if (this.a instanceof ListenerMulticaster<?>)
         {
            a2 = ((ListenerMulticaster<T>) this.a).remove(toRemove);
            if (this.b instanceof ListenerMulticaster<?>)
            {
               b2 = ((ListenerMulticaster<T>) this.b).remove(toRemove);
            }
            else
            {
               b2 = this.b; // B is not a multicaster.
            }
         }
         else
         { // A is not a multicaster.
            a2 = this.a;
            if (this.b instanceof ListenerMulticaster<?>)
            {
               b2 = ((ListenerMulticaster<T>) this.b).remove(toRemove);
            }
            else
            {
               return this; // A nor B are multicasters.
            }
         }
         return new ListenerMulticaster<T>(a2, b2);
      }

      /**
       * Passes the event on to children.
       */
      @Override
      public void eventFired(final T event)
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