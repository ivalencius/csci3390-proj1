# Large Scale Data Processing: Project 1

Authors:

- Ilan Valencius (valencig)

## Function to run
This eliminates logging extraneous information to the terminal.
```
spark-submit \
  --conf "spark.driver.extraJavaOptions=-Dlog4j.debug=false" \
  --conf "spark.executor.extraJavaOptions=-Dlog4j2.debug=false" \
  --class "project_1.main" \
  --master "local[*]" \
  target/scala-2.12/project_1_2.12-1.0.jar this_is_a_bitcoin_block_of_valencig 2 10000000
```

## 1 - Local machine
Running with the header string `this_is_a_bitcoin_block_of_valencig`. All code was run with 10,000,000 trials so every run took 4s to complete. To estimate the number of trials and time we divide the 10,000,000 trials and 4s run time by the found count. 

__Note__: For `k=6` a valid nonce was found only once after running 20 million trials.

| `k` | `xS` | Hash | Found Count | Number of trials | Time Elapsed [s] |
| --- | ---- | ---- | ----------- | ---------------- | ------------ |
|  2  | 951415069this_is_a_bitcoin_block_of_valencig | 00721ca69fc1b9d3e0a8962e833f9e32142da1dfb6e2b6cfc9139cb7e05f2b5c | 38,976 | 256 | $1.03\times 10^{-4}$ |
|  3  | 1162303637this_is_a_bitcoin_block_of_valencig | 000aaea50206926c0b6c203eef1aa0957f787f8b943c2426e558fef986093cd0 | 2409 | 4,151 | $1.66\times 10^{-3}$ |
|  4  | 1302133326this_is_a_bitcoin_block_of_valencig | 0000340a58b14ad543d629595281576a4de3827e3d5c43b97a514798ed75e6af | 151 | 66,225 | $2.65\times 10^{-2}$ |
|  5  | 1032985810this_is_a_bitcoin_block_of_valencig | 00000d5ab08dca207450a006e467355562b5ebf583baac5a4efb50e1d2f10172 | 12 | 833,333 | $3.33\times 10^{-1}$ |
|  6  | 1793104307this_is_a_bitcoin_block_of_valencig | 0000009990c2438e9ddecaabbf7fa5890f1d937cdf8ed0e57bf25c99a9773424 | 1 | 20,000,000 | $7.00$ |


## 2 - GCP
The configuration for my cluster is as follows:

__Master Node__
- _machine_: n2-standard-2
- _memory_: 50 GB

__Worker Nodes__
- _n-workers_: 3
- _machine_: n2-standard-2
- _memory_: 30 GB

To solve case `k=7` we pass the following arguments:

`this_is_a_bitcoin_block_of_valencig 7 100000000` (100,000,000 trials)

Similarly to the process on our local machine we divide the number of trials and the time elapsed by the found count to estimate the time to find one nonce which satisfies `k=7`. For 100,000,000 million trials, the correct nonce was only found once so the estimated number of trials and elapsed time was that of the execution.

| `xS` | Hash | Found Count | Number of trials | Time Elapsed [s] |
| ---- | ---- | ----------- | ---------------- | ------------ |
| 69436366this_is_a_bitcoin_block_of_valencig | 00000005fe23e6e83ca1a6dca4bf1d14d5da19f856ff0dd0f71121c3580f10d6| 1 | 100,000,000 | 267 s |

## 3 - Nonce Generation
To generate the nonce from 1 to the number of trials we use the following code.
```
val nonce = sc.range(1, trials+1)
```

For `trials=N` this code outputs nonces from [1, N]. We then rerun the code with the same parameters as in [Part 1](#1---local-machine). Like in Part 1, we divide the number of trials (10,000,000) and time elapsed (4s) by the found count to determine the time to find one valid nonce.

__Note__: For `k=6` a valid nonce was only found after running 30 million trials.
| `k` | `xS` | Hash | Found Count | Number of trials | Time Elapsed [s] |
| --- | ---- | ---- | ----------- | ---------------- | ------------ |
|  2  | 816this_is_a_bitcoin_block_of_valencig | 00e95c2441dca7846dad8a623050eae2ed119578a1623e36de60db6dda1718f3 | 39316 | 254 | $1.02\times 10^{-4}$ |
|  3  | 5427this_is_a_bitcoin_block_of_valencig | 0005180d1acb0870f4f92a62fd1c05e37779286a1febc1a66139733b222cf340 | 2561 | 3,904 | $1.56\times 10^{-3}$ |
|  4  | 21632this_is_a_bitcoin_block_of_valencig | 0000efe6a3d7d2542574f8e4b189a1a43eb7634efafbf83d3d32c48234ca34ed | 157 | 63,694 | $2.55\times 10^{-2}$ |
|  5  | 375963this_is_a_bitcoin_block_of_valencig | 00000b39a5f19eccb07ecb2f758d3ea50a41c11f444fa9398bdac622a175035f | 5 | 2,000,000 | $8.00 \times 10^{-1}$ |
|  6  | 27033512this_is_a_bitcoin_block_of_valencig | 000000a3986cf50effdcd0d6e878ff4626b5fefd9f78d9d8c83fb3c5ff4a9aaf | 1 | 30,000,000 | $9.00$ |

The non-randomized approach appears to be slower than the randomized approach. For `k=1-4` not much variation is seen between the two approaches. At higher difficulties, using a non-randomized approach is much slower (see the following table). 
| `k` | Trials (random) | Trials (not random) | Time elapsed (random) | Time elapsed (not random) |
| - | - | - | - | - |
| 5 | 833,333 | 2,000,000 | $3.33\times 10^{-1}$ s | $8.00 \times 10^{-1}$ s|
| 6 | 20,000,000 | 30,000,000 | $7.00$ s | $9.00$ s | 

To examine performance at high trial numbers (`k=6`), the randomized approach was tested 5 times for 30,000,000 trials - the number required to generate just one valid nonce for the non randomized approach. Across 5 different runs, the randomized approach found 8 nonces matching the criteria for an average of 1 nonce found per 18,750,000 trials, over 10 million less than the non-randomized approach. This finding supports the idea that randomly searching for the nonce is a more efficient strategy than searching linearly.