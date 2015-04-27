clist = function(x){
  list = vector("list", 2880)
  for(i in 1:length(x)-1){
    list[i] = x[i+1]-x[i]
  }
  list
}

write.csv(file="on.csv", x=on)
write.csv(file="off.csv", x=off)
write.csv(file="row.csv", x=row)

cl = cl[-1,]
cl = cl[,2:8]
write.csv(file="winter.csv", x=winter)
winter = read.csv("winter.csv")
off = read.csv("off.csv")
on= read.csv("on.csv")

temp = data.frame('InTemp'=matrix(unlist(list), nrow=2880, byrow=T),'DelTa'=matrix(unlist(delta),nrow=2880, byrow=T))
on = data.frame(out=matrix(),wind=matrix(),tank=matrix(),ins=matrix(),energy=matrix(),green=matrix(), delta=matrix())
off = data.frame(out=matrix(),wind=matrix(),tank=matrix(),ins=matrix(),energy=matrix(),green=matrix(), delta=matrix())



#DATA cleaning
winter = read.csv("winter.csv")
red=winter

##removes timesteps from 5pm till 12am
for(i in rev(seq(0,11520,96))){
  for(j in (i+95):(i+68)) 
    red = red[-j,]
}

#add new row to data frame
on = rbind(row, on[,1:8])

for(i in 1:10403){
  on[i,7]=(on[i,7]+4.0424)*16.6
}


##removes saturday and sunday in december
for(i in rev(seq(1,2107,476))){
  print(i)
  print(cl[i+204,])
 for(j in (i+203):(i+68)) 
   cl = cl[-j,]
}


##removes saturday and sunday in january and february
cl = cl[-c(1429:1496),]

for(i in rev(seq(1429,5000,476))){
  print(i)
  print(cl[i+204,])
  for(j in (i+340):(i+475)) 
    cl = cl[-j,]
}

##removes saturday and sunday in March 
for(i in rev(seq(4285,6000,476))){
  print(i)
  print(cl[i+204,])
  for(j in (i+339):(i+204)) 
    cl = cl[-j,]
}



##sorts negative and positive
j=1
for(i in 5475:1){
  if(on[i,7] < 0) {
    onNeg[j,] = on[i,]
    on = on[-i,]
    j = j+1
  }
}

j=0
for(i in 2931:1){
  if(on[i,7] < 0) {
    j = j+1
  }
}

#split timesteps by on and off groups
offc=1
onc=1
for(i in 1:5848){
  val = cl[i,7]
  if(val != 0){
    on[onc,]= cl[i,]
    onc = onc+1
  }
  else{    
    off[offc,]= cl[i,]
    offc= offc+1
  }
} 


#cleaning data further by making pattern

date = 12.01
c = 1
i=1
while(c != length(row[,9])){
  if(date == row[c+1,10]){
    if(row[c+1,9] < row[c,9]){
      cat(date," :: ",row[c+1,9]," < ",row[c,9],"i=",i,"\n")
      row = row[-(c+1),]
      i=i+1
    }
    else{
      c=c+1
    }      
  }
  else
  {
    c=c+1
    date = row[c,10]
  }
}

#REMOVE negative vlaues

for(i in 5475:1)
  if(row[i,9] < 0)
    row=row[-i,]
  


#DATA analysis
#pairs(x ~ y+z)

m1 = lm(delta~out+wind+tank+ins+energy+green, on)

plot(delta~out+wind+tank+ins+energy+green)

install.packages("e1071")
library(e1071)


attach(off)
names(off)

o1 = lm(delta~out+wind+tank+ins+energy+green)
summary(o1)


o2 = lm(delta~out)
summary(o2)

cor(delta,out+wind+tank+ins+energy+green)
attach(on)
detach(on)
m1 = lm(DeltaTemp~OutsideTemp+Wind+TankTemp+InsideTemp+EnergyCirculationPump+GreenEnergy)
m2 = lm(DeltaTemp~OutsideTemp+Wind+TankTemp+InsideTemp+EnergyCirculationPump+GreenEnergy,row)

model <- svm(delta ~ out+wind+tank+ins+green, off)

predictedY <- predict(m3, on)

points(on$energy, predictedY, col = "red", pch=4)


cor(DeltaTemp,0.0092 * OutsideTemp +
      0.0443 * TankTemp +
      22.3724 * EnergyCirculationPump +
      0.403  * GreenEnergy +
      -2.5942)

# Function that returns Root Mean Squared Error
rmse <- function(error)
{
  sqrt(mean(error^2))
}

# Function that returns Mean Absolute Error
mae <- function(error)
{
  mean(abs(error))
}


#cross validation for linear regression
install.packages("DAAG")
library(DAAG)

#on
m2 = lm(DeltaTemp~OutsideTemp+Wind+TankTemp+InsideTemp+EnergyCirculationPump+GreenEnergy, on)
mod1 <- cv.lm(df=on,m2,m=10)
mod2 <- cv.lm(df=on,DeltaTemp~OutsideTemp+Wind+TankTemp+InsideTemp+EnergyCirculationPump+GreenEnergy,m=10)

#rmse
sqrt(mean((mod1$cvpred - mod1$DeltaTemp)^2))
#mae
mean(abs(mod1$cvpred - mod1$DeltaTemp))

#off
m2 = lm(DeltaTemp~OutsideTemp+Wind+TankTemp+InsideTemp+EnergyCirculationPump+GreenEnergy, off)
mod1 <- cv.lm(df=off,m2,m=10)
mod2 <- cv.lm(df=off,DeltaTemp~OutsideTemp+Wind+TankTemp+InsideTemp+EnergyCirculationPump+GreenEnergy,m=10)

#rmse
sqrt(mean((mod1$cvpred - mod1$DeltaTemp)^2))
#mae
mean(abs(mod1$cvpred - mod1$DeltaTemp))

#RWeka
install.packages("RWeka")
library(RWeka)

on_model = M5P(DeltaTemp~OutsideTemp+TankTemp+InsideTemp+EnergyCirculationPump+GreenEnergy, data=on)
off_model = M5P(DeltaTemp~OutsideTemp+TankTemp+InsideTemp+GreenEnergy, data=off)
summary(m2)

pdt = predict(m1,row)
error = on$DeltaTemp - pdt
## Use 10 fold cross-validation.
e=evaluate_Weka_classifier(m1,numFolds = 10)
e
pdt = predict(m1,row)
error = test$DeltaTemp[1:100] - pdt

pdtOff2 = predict(off_model, r)

rmse(error)

#Settings for directories
Sys.setenv(JAVA_HOME='C:\\Program Files\\Java\\jre7') 

#SAVING models

rJava::.jcache(on_model$classifier)
save(on_model,file="on_model.rda")
load("on_model.rda")

rJava::.jcache(off_model$classifier)
save(off_model,file="off_model.rda")
load("off_model.rda")
summary(m1)

#Java integration
install.packages("rJava")
library(rJava)


r = data.frame(OutsideTemp=t,TankTemp=4,InsideTemp=5,EnergyCirculationPump=7,GreenEnergy=9)


for(i in 97:2881){
  s = i%%96+1
  t[s,2] = t[s,2]+test[i,2]
}
