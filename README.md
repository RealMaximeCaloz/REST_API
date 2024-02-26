# REST API Version of Instagram Post Generator

This is a REST API version of my Bulk Instagram Post Generator, using Spring Boot and Thymeleaf.

[You can find the code here.](https://github.com/RealMaximeCaloz/REST_API)

The REST API features a front-end which accepts a String and an image as inputs.

![api_inputs](https://github.com/RealMaximeCaloz/Portfolio/blob/17861c2ebc76d13eea9eb51ff4c8d721b5c5d1b0/pic1_rest_api.png)


The API adds the text to the image provided, and returns the processed image to the user on the front end.

![api_outputs](https://github.com/RealMaximeCaloz/Portfolio/blob/17861c2ebc76d13eea9eb51ff4c8d721b5c5d1b0/pic2_rest_api.png)

Only the text-addition of the Bulk Instagram Post Generator has been implemented because this is a proof of concept.

A configuration file has been created to override the addResourceHandlers() method from the WebMvcConfigurer interface.

This allows dynamically-generated images to be displayed onto the front-end to the user.
Otherwise, the images would not display, if generated into the static folder.
