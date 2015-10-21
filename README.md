API REST for JAVA Play Framework
================================

-----

Welcome to a API REST with Play Framework in JAVA.

For test the API you can use the file extracted from PostMan or use these URIs in your Rest Client.

All routes have the port  80. This can be changed in `application.conf` file.


| Method | URI | Data | HTTP Code | Response (in JSON) |
| ------ | --- | ---- | --------- | ------------------ |
| GET | /employees?page=2 |  | 200 | {"data":[{"id": 4,"name": "Josrom"},{"id": 4,"name": "Dantar"},...],"total":∞,"link-self": "/employees?page=2","link-prev": "/employees?page=1","link-next": "/employees?page=3"} |
| GET | /employees/1 |  | 200 | {"id": 1,"name": "Josrom"} |
| GET | /employees/6 |  | 404 | {"error":"Not found 6"} |
| POST | /employees | {"name": "new user"} | 201 | {"id": 6,"name": "new user"} |
| POST | /employees |  | 400 | {"name": ["This field is required"]} |
| PUT/PATCH | /employees | {"id": 1,"name": "new name"} | 200 | {"id": 1,"name": "new name"} |
| PUT/PATCH | /employees |  | 400 | {"name": ["This field is required"]} |
| DELETE | /employees/1 |  | 200 | {"msg":"Deleted 1"} |
| DELETE | /employees/0 |  | 404 | {"error":"Not found 0"} |

CHANGELOG
=========

## Version 1.1

- Changed Ebean ORM for JPA
- Added pagination
- Docblocks for all methods
- Added test

## Version 1.0

- App base