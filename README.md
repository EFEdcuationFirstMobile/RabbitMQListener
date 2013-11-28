Small utility to listen in the command line to RabbitMQ topics.

To make it easier I've uploaded the exported jar file so if you're not interested in the code just download the jar file.

Usage is:

java -jar rabbitListener.jar host port user password virtualhost exchangeName topic

Remember you can use * or # in topics to listen to one or several words