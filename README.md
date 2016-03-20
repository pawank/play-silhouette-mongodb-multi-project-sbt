Sample application 
=====================================

### Frameworks and libraries used  
Playframework 2.4  
Silhouette 3.0  
Mongodb 2.6  

### How to run the application?  

Mongodb should be running at host (mongodb) at port 27017.  
Add 127.0.0.1 (for ubuntu/linux) and 192.168.99.100 (or docker IP for docker mac osx) in /etc/hosts as follows:  
`127.0.0.1  mongodb`

Add hostname / IP mapping in /etc/hosts to access applications using hostname -   
`127.0.0.1  sample.com`  
`127.0.0.1  www.sample.com`  
`127.0.0.1  admin.sample.com`    

SBT jar is made available inside scripts folder,  

`mkdir -p ~/.sbt/launchers/0.13.8/`  
`cp scripts/sbt-launch.jar ~/.sbt/launchers/0.13.8/`  
`chmod a+x scripts/sbt`  
`./scripts/sbt`  
  
Inside SBT prompt,   
`$update  
$compile`  

You can view all sub-projects using `projects` command in sbt prompt.  
Run individul projects at different ports as follows:  
`$project web`  
`$run -Dhttp.port=9000`  

Admin/API using Silhouette over REST for authentication,  
`$project admin`    
`$run -Dhttp.port=10000`    

You can access at www.sample.com:9000 and admin.sample.com:10000.

### Test API authentication
curl -X POST http://localhost:9000/api/auth/signin/credentials -H 'Content-Type: application/json' -d '{"identifier": "test@test.com", "password": "password"}' -v  
  
curl -X GET http://localhost:9000/api/test -H 'X-Auth-Token:2-LKNBVqdpfkm6LvfLsmlKdmZ5A9KANU6QSGMZRKB+GwcTqEmZanHeefvTWC+PiNu+8veKsP3udgHZEs5oF25+w2ojUVRzPXgTvB9KJiVcwE22MG6W5WXg97u4OyLvTs+5AV1Z0nnEiJB/c4HWxYm+U2imOiCLhKLzgxwCaX47u4jCj9EMJshylc6rQ7Zx6pSjRs757GxaeW2FUE7chS6hl1+koX4PrgoUhaYGr4jm7MZJrf3GdUL2PqyvHU1SGtn8TWdMDN/8Olw7t4Zce+PdeoNYMCkI5l8CYVPY2c1qpgaQmDZqQMpQkzff8c7NJ46nVsh6IIY2a4I0CZWtF9f90L5LzYsnY0qE4iOKWlqSmq+ye2/nNhaL7RWm/HWwDHhXFs3UVglU8Xtlpa8YYmmcs9XoXS94qGeFFssfDCkajsRno2ZL+dWXMMWo1GqWxCVdaNtextWPq/R+k3a+g0Hu3vl31firc7Hbq6Z7fi0UixppuzjD6vuBEeXhqwIGzBf2KEtUKbHoZMJFHDsCNHtetOfTa3jGS3FzHl83407b/m8eIpwOGfbA1xT9/YdpdYZHa0wM3f4U2s1Ack6F4AklGAFS5huBvwDVlEUnfSO04CbIxO4h7rNcer2xbROpvagDy19h1Do0zNtac+/e4Ks3GtRDFld2Fw==' -v


### Reference  
It is highly recommended to go through the below tutorials to better understand the overall structure of the application and its usage.  

Silhouette examples [http://silhouette.mohiva.com/docs/examples]  
Play Silhouette reactivemongo [https://github.com/ezzahraoui/play-silhouette-reactivemongo-seed]  
Silhouette REST seed project [https://github.com/datalek/silhouette-rest-seed]  
Multidomain SBT project [https://github.com/adrianhurt/play-multidomain-seed]  
