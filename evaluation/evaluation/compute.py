from math import sqrt

__author__ = 'nherbaut'

import os
from os import listdir
from os.path import isfile, join


def handle_dir(dir):
    res = {}
    for res_file in [f for f in listdir(dir) if isfile(join(dir, f))]:
        print res_file
        delays = []
        latencies = []
        with open(os.path.join(dir, res_file)) as f:
            lines = f.readlines()
        for line in lines:
            delay, http_code, thread_group, thread_id, latency = line.split(",")
            delays.append(float(delay))
            latencies.append(float(latency))
        delay_avg = sum(delays) / float(len(delays))
        latency_avg = sum(latencies) / float(len(latencies))
        res[res_file] = (delay_avg, sqrt(reduce(lambda acc, d: acc + (d - delay_avg) ** 2, delays)), latency_avg,
                         sqrt(reduce(
                             lambda acc, l: acc + (latency_avg - l) ** 2, latencies)))

    return res


res = {}
for dir in os.listdir("."):
    try:
        if (int(dir)):
            res[dir] = handle_dir( dir)
    except ValueError:
        pass


for j in range(0,4):
    print "--------------   "
    for item in sorted(res.items()):
        toprint = []
        toprint.append(item[0])
        toprint += [i[j] for i in item[1].values()]

        print "%10s\t%.2f\t%.2f\t%.2f\t%.2f\t" % tuple(toprint)





