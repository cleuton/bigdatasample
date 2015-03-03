# bigdatasample
A Hadoop MapReduce analysis using tweets. Includes a twitter parser and a MapReduce project. For use in AWS EMR.

Analyzing non structured text requires NLP processing. Unfortunatelly there are no suitable processors for Portuguese, so, this project uses SentiWordNet (http://sentiwordnet.isti.cnr.it), and the Google Translate API. 
It collect tweets, translate them to english, and run a sentiment analysis using SentiWordNet file.


