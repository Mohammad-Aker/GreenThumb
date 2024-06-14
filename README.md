<a name="readme-top"></a>
<div align="center">
  <img src="1.jpg" alt="Description of the image">
</div>
<div align="center">
  <br>
  <h1>Greenthumb 🌱</h1>
  <strong>Welcome to GreenThumb, Cultivating urban green spaces and sustainable living communities!</strong> &nbsp;<br>
  <a href="https://github.com/Mohammad-Aker/GreenThumb"><strong>Check out the documentation »</strong></a>
</div>
<br>
<div align="center">
  <p align="center">
    <a href="Demo Link">👾 View Demo</a>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://github.com/Mohammad-Aker/GreenThumb/issues/new">🐞 Report Bug </a>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://github.com/Mohammad-Aker/GreenThumb/issues">🧩 Request Feature</a>
  </p>
</div>
<br>
<div align="center">
  <img src="2.jpg" alt="Description of the image">
</div>
<br>
<br>



<details>
  <summary><h2>🌿 Table of Contents<h2\></summary>
  <ol>
    <li><a href="#intro">Introduction (What's Greenthumb?)</a></li>
    <li><a href="#coref">Core Features</a></li>
    <li><a href="#addf">Additional Features</a></li>
    <li><a href="#roles">Roles</a></li>
    <li><a href="#bw">Built With</a></li>
    <li><a href="#gs">Getting Started</a></li>
    <li><a href="#API">API Documentation</a></li>
    <li><a href="#demo">Demo</a></li>
    <li><a href="#contribution">Contribution</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>
 <br>



<a name="intro"></a>
## 🌳 What is Greenthumb?
<strong>GreenThumb</strong> is a platform designed to promote urban gardening, sustainable living, and community-driven food production. It serves as a hub for individuals, communities, and organizations to collaborate, share knowledge, and access resources related to urban gardening and sustainable living practices.
<br>
<br>
<br>



<a name="coref"></a>
## 🌾 Core Features
<strong>1. Community Gardens:</strong> Directory of gardens with locations, available plots, and growing conditions to help users join local gardening. <br>
<strong>2. Crop Planning and Tracking:</strong> Tools for planning and monitoring activities like crop rotations, schedules, and harvests.<br>
<strong>3. Knowledge Sharing:</strong> Library of guides and tutorials from experienced gardeners and organizations.<br>
<strong>4. Material Exchange:</strong> Platform for sharing resources such as tools, seeds, compost, and surplus produce.<br>
<strong>5. Volunteer Coordination:</strong> Organizes volunteers for garden maintenance, events, and workshops.<br>
<strong>6. Local Partnership Integration:</strong> Connects with local nurseries, farms, and organizations to promote their products and events.<br> <br>
▶️ For more details on <strong>Core Features</strong> visit the <a href="https://github.com/Mohammad-Aker/GreenThumb/wiki">Wiki</a> section.
 <br>
 <br>
 <br>
 <br>

 
<a name="addf"></a>
## 🐞 Additional Features
<strong>1. Chat System: </strong>Events group chats enable volunteers and representatives to communicate within event-specific group chats in real-time , enhancing coordination and collaboration within the event community.<br>
<strong>2. External API Integration: </strong> Utilized to enhance functionality and supplement data collection.<br>
<strong>3. Security: </strong>Privacy, security and encryption are provided as all passwords are encrypted. For more details visit our <a href="https://github.com/Mohammad-Aker/GreenThumb/wiki">Wiki.</a> <br>
<strong>4. Testing: </strong> Guaranteeing a robust and stable platform for crafting project management.<br>
<strong>5. Docker: </strong> Simplifies software deployment by packaging applications into portable containers.<br>

 <br>
 <p align="right">(<a href="#readme-top">⬆️ Back to top</a>)</p>
 <br>
 <br>


<a name="roles"></a>
## 🕵️‍♂️ Roles
Roles are actually like digital security guards. They check user IDs and say "yes" or "no" to some actions, like deleting gardens or viewing other people's stuff. This keeps everyone safe and in their own areas of the app. In this project, we implemented the following roles:
* <strong>Admin:</strong> This role has the most extensive permissions, including managing all aspects of community gardens (create, view, update, delete), approving or rejecting garden applications, and accessing all user data.<br>
* <strong>Representative:</strong> This role can manage events, including creating and deleting them. They seem to be associated with specific community gardens.<br>
* <strong> Expert: </strong>Experts can create, view, update and delete crops and planting schedules, as well as access soil and weather data related to the crops. They can also search for materials and exchanges.<br>

* <strong>User: </strong>Users can create, view, update and delete their own crops and planting schedules, join and leave community gardens, search for materials and exchanges, and view their harvest records.<br>
* <strong> Volunteer:</strong> Volunteers can view all volunteering opportunities, create and delete their own volunteering registrations, and participate in event volunteering.<br>
 <br>
 <p align="right">(<a href="#readme-top">⬆️ Back to top</a>)</p>
 <br>
 <br>

 
<a name="bw"></a>
## 🏡 Built With
* [![SpringBoot][Spring-boot]][SpringURL] <br>An open-source Java framework for creating stand-alone, production-grade applications.
* [![Google Cloud SQL][GoogleCloud]][GoogleCloudURL] <br>A fully-managed relational database service provided by Google Cloud Platform.
* [![Rabbitmq][Rabbit]][RabbitURL] <br>An open-source message broker software implementing the Advanced Message Queuing Protocol (AMQP).
* [![Docker][Docker]][DockerURL] <br>A platform for building, sharing, and running applications in containers.
* [![Postman][Postman]][PostmanURL] <br>A collaboration platform for designing, testing, and documenting APIs.
* [![Github][Github]][GithubURL] <br>A web-based platform for version control and collaboration using Git.
<br>
<p align="right">(<a href="#readme-top">⬆️ Back to top</a>)</p>
<br>




<a name="gs"></a>
## 🚀 Getting Started
### ⚙️ Running the project
#### To get started with the project:
##### 1. Clone the repository:
> [![Github][Github]][wewe]
>
> ```sh
> git clone https://github.com/Mohammad-Aker/GreenThumb
> ```
##### 2. Configure the Database:
Access the Google Cloud SQL instance and set up the database configurations in `application.properties`.
##### 3. Run the Backend:
>
> ```sh
> mvn spring-boot:run
> ```
##### 4. Setup RabbitMQ:
* Ensure RabbitMQ server is running.
* Configure RabbitMQ settings in `application.properties`.

<br>
<p align="right">(<a href="#readme-top">⬆️ Back to top</a>)</p>
<br>



<a name="API"></a>
## 📄 API Documentation
The API is comprehensively documented using Postman. Access the documentation by navigating to <a href="https://documenter.getpostman.com/view/36132853/2sA3XPC2vz?fbclid=IwZXh0bgNhZW0CMTAAAR0tcu44KIqRbp8HjjfAHRF1QnBkxzFu8aDxbYGTza4pRAlS5zytNN74sfM_aem_AWDZfjoZP2dm1OYWxrCSFApKQxj7LCv7K9Fj4wvIIK0pLRxTInh2JZzl8CrIi7Wgra5mWrNom5ehE6UdqLtPbkHe"><strong>API documentation</strong></a> once the backend is operational. This documentation covers all available endpoints, request parameters, response formats, and example requests and responses.

<br>
<p align="right">(<a href="#readme-top">⬆️ Back to top</a>)</p>
<br>
<br>


<a name="demo"></a>
## 🎥 Demo
Check out our project demo to see it in action! Click the link here to experience the magic firsthand. <a href="Demo Link">🚀 View Demo</a>
<br>
<p align="right">(<a href="#readme-top">⬆️ Back to top</a>)</p>
<br>
<br>
<br>


<a name="contribution"></a>
## :wave: Contributing 
<p align="right"> <a href="https://github.com/Mohammad-Aker/GreenThumb/graphs/contributors"><img src="https://img.shields.io/github/contributors/Mohammad-Aker/GreenThumb" alt="contributors" /></a> </p>
<p align="center"><a href="https://github.com/Mohammad-Aker/GreenThumb/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Mohammad-Aker/GreenThumb" />
</a> </p>
<p align="center"> <strong>Contributions are always welcome!</strong> </p>
<p align="center"> You can start your contribution journey by reading the <a href="https://github.com/Mohammad-Aker/GreenThumb/blob/main/Contribution.md">Contribution</a> document 🎉 </p>
<br>
<br>


<a name="contact"></a>
## ☎️ Contact

* Lama Ibrahim - lama.ibrahim@gmail.com
* Mohammed Aker - mohammadaker7@gmail.com
* Shahd Salahat - Shahd.salahat@gmail.com
* Dana Breik - Dana.breik@gmail.com
<p align="right">(<a href="#readme-top">⬆️ Back to top</a>)</p>


<br>
<br>



















<!-- MARKDOWN LINKS & IMAGES -->
[Spring-boot]: https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white
[SpringURL]: https://spring.io/projects/spring-boot
[Docker]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[DockerURL]: https://www.docker.com/
[GoogleCloud]: https://img.shields.io/badge/Google%20Cloud%20SQL-4285F4?style=for-the-badge&logo=google-cloud&logoColor=white
[GoogleCloudURL]: https://cloud.google.com/?hl=en
[Rabbit]: https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white
[RabbitURL]: https://www.rabbitmq.com/
[Github]: https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white
[GithubURL]: https://github.com/
[Postman]: https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white
[PostmanURL]: https://www.postman.com/
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[wewe]: https://github.com/Mohammad-Aker/GreenThumb
[JQuery-url]: https://jquery.com 
