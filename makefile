JCC = javac # define a makefile variable for the java compiler
JFLAGS = -g
default: PreProcessing.class FeatureVector.class
PreProcessing.class: PreProcessing.java
	$(JCC) $(JFLAGS) PreProcessing.java
FeatureVector.class: FeatureVector.java
	$(JCC) $(JFLAGS) FeatureVector.java
clean: 
	$(RM) *.class