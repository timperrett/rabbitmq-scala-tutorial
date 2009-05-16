package eu.getintheloop.example.rabbit

import _root_.com.rabbitmq.client.{ConnectionFactory,ConnectionParameters}
import _root_.net.liftweb.amqp.{AMQPSender,StringAMQPSender,AMQPMessage}
import _root_.scala.actors._

/**
 * By default, lift-amqp comes with a basic class serilizer for string AMQP 
 * messages, if you want to serilize / deserilize (send) other objects over 
 * the wire then implementing your own AMQPSender is relitivly easy.
 * 
 * @see net.liftweb.amqp.StringAMQPSender
 */
object BasicStringSender {
  val params = new ConnectionParameters
  // All of the params, exchanges, and queues are all just example data.
  params.setUsername("guest")
  params.setPassword("guest")
  params.setVirtualHost("/")
  params.setRequestedHeartbeat(0)
  val factory = new ConnectionFactory(params)
  // Create a new instance of the string sender.
  // This sender will send messages to the "mult" exchange with a 
  // routing key of "routeroute"
  val amqp = new StringAMQPSender(factory, "localhost", 5672, "mult", "routeroute")
  amqp.start
  
  /**
   * Salute the rabbit!
   */
  def salute = amqp ! AMQPMessage("hey there!")
}

