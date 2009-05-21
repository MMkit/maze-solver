package maze.util;

/**
 * This Listener interface allows the creation of Listener classes that can
 * register themselves with a ListenerSubject.
 * @author Luke Last
 * @param <T> The type of the event object that is passed in when the event is
 *           fired. This type should match the defined generic type of the
 *           ListenerSubject you want to register with.
 */
public interface Listener<T>
{
   /**
    * This is called when the ListenerSubject wants to notify the registered
    * Listeners.
    * @param event The event object.
    */
   public void eventFired(T event);
}