
from hashlib import sha1
import http.client
import hmac
import json

#32-bit key for signing our messages - dev
auth_key = "7d28d176792b29f73855dbdef2d7d5b929315ab2b89e0225aaa52c61513e9edb"

server_url = "localhost"
server_port = 8000


data = {"test" : "TEST"}


conn = http.client.HTTPConnection(server_url, server_port)

request_data = json.dumps(data)
mac = hmac.new(str.encode(auth_key), str.encode(request_data), sha1)

conn.request("PUT", "/linux", request_data, headers={"Auth": mac.hexdigest()})

response = conn.getresponse()

print(request_data)
print(mac.hexdigest())
print(response.status)
