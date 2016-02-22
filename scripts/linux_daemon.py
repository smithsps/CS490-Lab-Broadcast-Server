"""
CS490 - Lab Broadcast daemon
Reports the computers current users and other details on a fixed interval

"""

import http.client
from hashlib import sha1
import hmac
import json
import subprocess
import time

#32-bit key for signing our messages - dev
auth_key = "7d28d176792b29f73855dbdef2d7d5b929315ab2b89e0225aaa52c61513e9edb"

#Server Settings
server_url = "localhost"
server_port = 8000


def get_computer_stats():
    w_out = subprocess.check_output(['w']).decode('utf-8').split('\n')
    w_header = w_out[0].split(' ')
    w_users = w_out[2:-1]

    # Start Composing Data Dictionary
    data = dict()
    data['time'] = int(time.time()) # Current time, epoch seconds

    with open('/proc/uptime', 'r') as f:
        data['uptime'] = int(float(f.readline().split()[0]))

    data['loadavg'] = dict()
    with open('/proc/loadavg', 'r') as f:
        f_out = f.readline().split()
        data['loadavg']['one'] = f_out[0]
        data['loadavg']['five'] = f_out[1]
        data['loadavg']['ten'] = f_out[2]
        data['loadavg']['current_processes'] = f_out[3]
        data['loadavg']['total_processes'] = f_out[4]

    """# Total Number of Users
    for item in s.split(','):
        print(item)
        if 'users' in item:
            data['total_users'] = int(item.strip().split(' ')[0]))
            break"""

    data['users'] = list()

    unique_users = set()
    for line in w_users:
        line = [l for l in line.split(' ') if l != '']
        print(line)
        user = dict()
        user['user'] = line[0]
        unique_users.add(line[0])

        user['tty'] = line[1]
        user['from'] = line[2]
        user['login@'] = line[3]
        user['idle'] = line[4]
        user['jcpu'] = line[5]
        user['pcpu'] = line[6]
        user['what'] = line[7]

        data['users'].append(user)


    data['total_users'] = len(w_users)
    data['total_unique_users'] = len(unique_users)

    print(json.dumps(data,sort_keys=True, indent=4, separators=(',', ': ')))


def send_authed_data(data):
    conn = http.client.HTTPConnection(server_url, server_port)

    request_data = json.dumps(data)
    mac = hmac.new(str.encode(auth_key), str.encode(request_data), sha1)

    conn.request("PUT", "/linux", request_data, headers={"Auth": mac.hexdigest()})
    return conn.getresponse()


def daemonize():
    # UNIX double forking.
    # Referenced from http://www.jejik.com/articles/2007/02/a_simple_unix_linux_daemon_in_python/
	try:
		pid = os.fork()
		if pid > 0:
			sys.exit(0) # exit first parent
	except OSError as err:
		sys.stderr.write('daemon fork #1 failed: {0}\n'.format(err))
		sys.exit(1)

	# decouple from parent environment
	os.chdir('/')
	os.setsid()
	os.umask(0)

	# do second fork
	try:
		pid = os.fork()
		if pid > 0:
			sys.exit(0) # exit from second parent
	except OSError as err:
		sys.stderr.write('daemon fork #2 failed: {0}\n'.format(err))
		sys.exit(1)

	# redirect standard file descriptors
	sys.stdout.flush()
	sys.stderr.flush()
	si = open(os.devnull, 'r')
	so = open(os.devnull, 'a+')
	se = open(os.devnull, 'a+')

	os.dup2(si.fileno(), sys.stdin.fileno())
	os.dup2(so.fileno(), sys.stdout.fileno())
	os.dup2(se.fileno(), sys.stderr.fileno())


#daemonize()
    
get_computer_stats()
