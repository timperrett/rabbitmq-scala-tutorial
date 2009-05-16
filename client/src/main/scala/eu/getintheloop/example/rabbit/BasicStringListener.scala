package eu.getintheloop.example.rabbit

import _root_.com.rabbitmq.client.{ConnectionFactory,ConnectionParameters}
import _root_.net.liftweb.amqp.{ExampleSerializedAMQPDispatcher,AMQPAddListener,AMQPMessage}
import _root_.scala.actors._

object BasicStringListener {
  val params = new ConnectionParameters
  params.setUsername("guest")
  params.setPassword("guest")
  params.setVirtualHost("/")
  params.setRequestedHeartbeat(0)

  val factory = new ConnectionFactory(params)
  // thor.local is a machine on your network with rabbitmq listening on port 5672
  val amqp = new ExampleSerializedAMQPDispatcher[String](factory, "localhost", 5672)
  amqp.start

  // Example Listener that just prints the String it receives.
  class StringListener extends Actor {
    def act = {
      react {
        case msg@AMQPMessage(contents: String) => println("received: " + msg); act
      }
    }
  }
  val stringListener = new StringListener()
  stringListener.start
  amqp ! AMQPAddListener(stringListener)
}
