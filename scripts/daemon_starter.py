
import subprocess

for i in range(0, 25):
    print('moore{:02d}.cs.purdue.edu'.format(i))
    try:
        subprocess.check_output(['ssh', 'moore{:02d}.cs.purdue.edu'.format(i), 'python3', 'projects/CS490-Lab-Broadcast-Server/scripts/linux_stat_daemon.py', '-u', 'mc15', '-p', '5000'], timeout=3)
    except Exception:
        pass
