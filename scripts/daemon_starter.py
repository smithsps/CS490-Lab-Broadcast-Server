#!/usr/bin/python3
"""CS490 - Lab Broadcast daemon deployer
SSH's into each desinated computer and starts the daemon script.

"""

import subprocess


def start_lab(lab_str, capacity):
	for i in range(1, capacity + 1):
	    print(lab_str.format(i) + '.cs.purdue.edu')
	    try:
	        pass
	        subprocess.check_output(['ssh', lab_str.format(i) + '.cs.purdue.edu', 
	        						 'python3', 'projects/CS490-Lab-Broadcast-Server/scripts/linux_stat_daemon.py', 
	        						 '-u', 'mc15', '-p', '5000'], timeout=3)
	    except Exception:
	        pass


start_lab('moore{:02d}', 24)
start_lab('sslab{:02d}', 24)
start_lab('borg{:02d}', 24)
start_lab('xinu{:02d}', 21)

start_lab('pod1-{:d}', 5)
start_lab('pod2-{:d}', 5)
start_lab('pod3-{:d}', 5)
start_lab('pod4-{:d}', 5)
start_lab('pod5-{:d}', 5)
