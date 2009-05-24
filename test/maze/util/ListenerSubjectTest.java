package maze.util;

import static org.junit.Assert.*;

import maze.util.ListenerSubject;

import org.junit.Test;

/**
 * @author Luke Last
 */
public class ListenerSubjectTest
{
   final MessageListener l1 = new MessageListener("L1");
   final MessageListener l2 = new MessageListener("L2");
   final MessageListener l3 = new MessageListener("L3");

   private void reset()
   {
      this.l1.message = null;
      this.l2.message = null;
      this.l3.message = null;
   }

   @Test
   public void testAll()
   {
      String message;

      final ListenerSubject<String> model = new ListenerSubject<String>()
      {};
      this.reset();
      model.notifyListeners("None");
      assertNull(l1.message);
      assertNull(l2.message);
      assertNull(l3.message);

      message = "Just L1";
      this.reset();
      System.out.println("## Add L1 ##");
      model.addListener(l1);
      model.notifyListeners(message);
      assertEquals(message, l1.message);
      assertNull(l2.message);
      assertNull(l3.message);

      message = "None";
      this.reset();
      System.out.println("## Remove L1 ##");
      model.removeListener(l1);
      model.notifyListeners(message);
      assertNull(l1.message);
      assertNull(l2.message);
      assertNull(l3.message);

      message = "L1 and L2";
      this.reset();
      System.out.println("## Add L1 and L2 ##");
      model.addListener(l1);
      model.addListener(l2);
      model.notifyListeners(message);
      assertEquals(message, l1.message);
      assertEquals(message, l2.message);
      assertNull(l3.message);

      message = "L1 and L2 and L3";
      this.reset();
      System.out.println("## Add L3 ##");
      model.addListener(l3);
      model.notifyListeners(message);
      assertEquals(message, l1.message);
      assertEquals(message, l2.message);
      assertEquals(message, l3.message);

      message = "L2 and L3";
      this.reset();
      System.out.println("## Remove L1 from beginning ##");
      model.removeListener(l1);
      model.notifyListeners(message);
      assertNull(l1.message);
      assertEquals(message, l2.message);
      assertEquals(message, l3.message);

      message = "L2 and L3 and L1";
      this.reset();
      System.out.println("## Add L1 ##");
      model.addListener(l1);
      model.notifyListeners(message);
      assertEquals(message, l1.message);
      assertEquals(message, l2.message);
      assertEquals(message, l3.message);

      message = "L2 and L3";
      this.reset();
      System.out.println("## Remove L1 from end ##");
      model.removeListener(l1);
      model.notifyListeners(message);
      assertNull(l1.message);
      assertEquals(message, l2.message);
      assertEquals(message, l3.message);

      message = "L2 and L3 and L1";
      this.reset();
      System.out.println("## Add L1 ##");
      model.addListener(l1);
      model.notifyListeners(message);
      assertEquals(message, l1.message);
      assertEquals(message, l2.message);
      assertEquals(message, l3.message);

      message = "L2 and L1";
      this.reset();
      System.out.println("## Remove L3 from middle ##");
      model.removeListener(l3);
      model.notifyListeners(message);
      assertEquals(message, l1.message);
      assertEquals(message, l2.message);
      assertNull(l3.message);

   }

   /**
    * Test method for
    * {@link maze.util.ListenerSubject#notifyListeners(java.lang.Object)}.
    */
   @Test
   public void testNotifyListeners() throws Exception
   {
      final ListenerSubject<String> model = new ListenerSubject<String>()
      {};
      model.notifyListeners("Message");
   }

   /**
    * Test method for
    * {@link maze.util.ListenerSubject#addListener(maze.util.ListenerSubject.Listener)}.
    */
   @Test
   public void testAddListener() throws Exception
   {
      final ListenerSubject<String> model = new ListenerSubject<String>()
      {};
      model.addListener(null);
   }

   /**
    * Test method for
    * {@link maze.util.ListenerSubject#removeListener(maze.util.ListenerSubject.Listener)}.
    */
   @Test
   public void testRemoveListener() throws Exception
   {
      final ListenerSubject<String> model = new ListenerSubject<String>()
      {};
      model.removeListener(null);
   }

   private static class MessageListener implements Listener<String>
   {
      public String message;
      private final String me;

      public MessageListener(String me)
      {
         this.me = me;
      }

      @Override
      public void eventFired(String event)
      {
         System.out.println(this.me + ": " + event);
         this.message = event;
      }
   }

}
