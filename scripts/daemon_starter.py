
import subprocess

for i in range(0, 21):
	s = subprocess.check_output(['ssh', 'moore%0d.cs.purdue.edu'.format(i), 'python3', 'projects/CS490-Lab-Broadcast-Server/scripts/linux_stat_daemon.py', '-u', 'mc15', '-p', '5000'])
