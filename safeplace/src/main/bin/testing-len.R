#!/usr/bin/env Rscript
library(ggplot2)
args <- commandArgs(T)
par(mar=c(3, 3.2, 1, 1),mgp=c(1.8,0.6,0), cex=1.3)
d <- read.table(args[1], header=T, sep=";",quote="")

m1 = median(d[which(d$testing=="safePlace"),] $sloc)
m2 = median(d[which(d$testing=="btrPlace"),] $sloc)
factor = m2 / m1
cat("median sloc. per testing campaign", m1,"\n")
cat("median sloc. per legacy unit test", m2,"\n")
cat("Reduction factor: ",factor,"\n")

d$testing = with(d, factor(testing, levels = rev(levels(testing))))

p <- ggplot(d, aes(sloc, color=testing)) + stat_ecdf(geom="step", size=1.1)
p <- p + xlab("length (sloc.)") + ylab("ratio") + theme_bw()
p <- p + theme(legend.justification=c(1,0), legend.position=c(1,0))

big = element_text(size = 19, family="Times")
med = element_text(size = 16, family="Times")
p <- p + theme(axis.text = med, axis.title = big, axis.title = big, legend.title=big, legend.text=med)
p <- p + scale_color_manual(values = c("#31a354", "#bdbdbd"), name="environment")
ggsave(args[2],p,width=6,height=4)