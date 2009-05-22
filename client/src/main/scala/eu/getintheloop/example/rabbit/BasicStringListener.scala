package eu.getintheloop.example.rabbit

import _root_.com.rabbitmq.client.{ConnectionFactory,ConnectionParameters,Channel}
import _root_.net.liftweb.amqp.{AMQPDispatcher,AMQPAddListener,AMQPMessage,SerializedConsumer}
import _root_.scala.actors._

class DemonstrationSerializedAMQPDispatcher[T](queueName: String, factory: ConnectionFactory, host: String, port: Int) extends AMQPDispatcher[T](factory, host, port) {
  override def configure(channel: Channel) {
    // Get the ticket.
    val ticket = channel.accessRequest("/data")
    // Set up the exchange and queue
    channel.exchangeDeclare(ticket, "mult", "fanout")
    channel.queueDeclare(ticket, queueName)
    channel.queueBind(ticket, queueName, "mult", "example.*")
    // Use the short version of the basicConsume method for convenience.
    channel.basicConsume(ticket, queueName, false, new SerializedConsumer(channel, this))
  }
}

class BasicStringListener(queueName: String) {
  val params = new ConnectionParameters
  params.setUsername("guest")
  params.setPassword("guest")
  params.setVirtualHost("/")
  params.setRequestedHeartbeat(0)
  
  val factory = new ConnectionFactory(params)
  val amqp = new DemonstrationSerializedAMQPDispatcher[String](queueName, factory, "xmpiemacbookpro", 5672)
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

