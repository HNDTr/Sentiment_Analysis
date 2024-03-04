# SentimentAnalysis
Sentiment Analysis Program


## Usage
---
### Compile the files

```
javac SentimentAnalysis.java
javac SentimentAnalysisBest.java
```

### Run the programs (with evaluation mode)
```
java SentimentAnalysis/SentimentAnalysisBest evaluation

```

## Descriptions
The sentiment analysis program utilizes a Naive Bayes classifier to determine whether a given text is positive or negative. It operates on reviews gathered from the RateItAll website, covering various domains such as movies, music, books, and politicians. Reviews are stored in individual text files, named according to the format: domain-number_of_stars-id.txt. For example, "movies-1-32.txt" contains a one-star movie review with an ID of 32.

The program offers two modes: interactive and evaluation. In interactive mode, users can input text via prompts and the program classifies it as positive or negative. In evaluation mode, users provide the name of a folder containing documents to classify. The program then categorizes these documents (restricted to those rated 1 or 5) as positive or negative and provides performance statistics on the results.

## Diff between SentimentAnalysis and SentimentAnalysisBest
The SentimentAnalysisBest application encompasses all features of sentiment analysis, incorporating the implementation of n-grams ranging from 1 to 8, and includes the removal of stop words.


## Acknowledgement 
---
### RateItAll (test and train data)

