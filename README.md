# Software Engineering Project Starter Code

This repo will start you off with an initial configuration that you'll modify as part of Checkpoint 1. As part of the modifications, you'll eventually delete the contents of this README and replace it with documentation for your project.

### API Diagram

![API Diagram](https://raw.githubusercontent.com/CPS353-Suny-New-Paltz/project-starter-code-koleada/refs/heads/main/APIDiagram.jpg)


### Compute Engine

For the computation engine, I chose to perform a simplified version of the PBKDF2 hashing algorithm. This hashing function is designed to be computationally expensive to make the resulting hashes harder to crack. 

We use a nested loop with a set number of iterations where on the first iteration we take the SHA-256 hash of the original user input and update a value. On subsequent iterations we continue to take the hash of the updated value until the loop completes. 

After all iterations are completed, we take the first four bytes of the final hash and use modulo to convert it into a positive integer (0 < hash < Interger.MAX_VALUE). To ensure these constraints we use 'Integer.floorMod() which ensures a nonnegative result. This essentially takes the floor of resultHash mod Integer.MAX_VALUE-1 then adding one to that. 

Example:

Input: 12345
Iterations: 1000 outer, 10 inner = 10,000 total (this can be changed to make it more or less computationally intense)
Output: 1890392330

### Network API

This API is multithreaded to allow multiple users to initiate computation requests. I chose a maximum of 8 threads as my CPU has 6 physical cores and 12 threads in total. 8 is a 
good middle ground to maximize efficiency from multithreading without overwhelming the CPU and reducing efficiency. I also made the number of threads slightly dynamic, so my code will choose the max number of threads by looking at the number of processors on the client machine and taking the minimum of that and 8. 