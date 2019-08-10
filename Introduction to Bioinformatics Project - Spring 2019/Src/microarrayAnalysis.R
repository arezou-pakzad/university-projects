#### set working directory 
setwd("/Users/arezou/Documents/University/Introduction to Bioinformatics/Project/Workspace/")

#### load libraries
library(Biobase)
library(GEOquery)
library(limma)
library(pheatmap)
library(gplots)
library(ggplot2)
library(reshape2)
library(plyr)
library(Rtsne)

series <- "GSE48558"
platform <- "GPL6244"


#### load series and platform data from GEO
gset <- getGEO(series, GSEMatrix =TRUE, AnnotGPL=TRUE, destdir = "Data/")
if (length(gset) > 1) idx <- grep(platform, attr(gset, "names")) else idx <- 1
gset <- gset[[idx]]


#### group names for all samples
gr <- c(rep("AML", 13), rep("X", 27), "Normal", rep("X", 3), "Normal", rep("X", 23), "Normal", "X", "Normal",
        rep("X", 3), "Normal", "X", rep("Normal", 4), "X", "Normal", rep("X", 2), rep("Normal", 2), 
        rep("X", 2), rep("Normal", 2), "X", "Normal", "X", "Normal", "X", "Normal", "X", "Normal", "X", "Normal",
        rep("X", 3), "Normal", rep("X", 3), "Normal", rep("X", 29), rep("Normal", 7), rep("AML", 2), "Normal",
        rep("AML", 3), rep("Normal", 20))

gr1 <- c(rep("AML", 13), rep("B_CL", 4), rep("T_CL", 2), "B_CL", "T_CL", rep("B_CL", 2), "T_CL", rep("B_CL", 2), rep("T_CL", 2), rep("B_CL", 5), rep("T_CL", 2),
         "B_CL", "T_CL", "B_P", "T_CL", "AML_CL", "Normal_G", "B_P", "T_CL", "AML_CL", "Normal_G", "B_P", "T_CL", "AML_CL", "B_P", "AML_CL", "AML_CL", "B_P", "AML_CL", "AML_CL",
         "B_P", "AML_CL", "AML_CL", "B_P", "B_CL", "AML_CL", "B_P", "B_CL", "AML_CL", "B_P", "B_CL", "AML_CL","B_P", "B_CL", "Normal_B", "B_CL", "Normal_T",
        "AML_CL", "B_P", "B_CL", "Normal_G", "B_CL", "Normal_G", "Normal_M", "Normal_M", "Normal_B", "B_CL", "Normal_T", "AML_CL", "B_CL", rep("Normal_T", 2), "AML_CL", "B_CL",
        rep("Normal_T", 2), "AML_CL", "Normal_B", "B_CL", "Normal_T", "AML_CL", "Normal_B", "B_CL", "Normal_T", "AML_CL", "Normal_CD34", "T_CL", "T_P", "AML_CL",
        "Normal_CD34", "T_CL", "T_P", "AML_CL", "Normal_CD34", "T_CL", "T_P", "AML_CL", "T_P", rep("B_P", 9), "T_P", rep("B_P", 7), rep("T_P", 8),rep("Normal_G", 7), 
        rep("AML", 2), "Normal_T", rep("AML", 3), rep("Normal_B", 7), "Normal_T", rep("Normal_M", 4), "Normal_G", rep("Normal_T", 7))


#### expression matrix
ex <- exprs(gset)

#### log2 scale, if required
# ex <- log2(ex + 1)
# exprs(gset) <- ex

#### boxplot
pdf("Results/boxplot.pdf", width = 170)
boxplot(ex)
dev.off()

#### normalize if required
# ex <- normalizeQuantiles(ex)
# exprs(gset) <- ex

#### correlation heatmap
pdf("Results/CorHeatmap.pdf", width = 15, height = 15)
pheatmap(cor(ex), labels_row = gr, labels_col = gr, border_color = NA, color = greenred(256))
pheatmap(cor(ex), labels_row = gr1, labels_col = gr1, border_color = NA, color = greenred(256))
dev.off()

#### principal components analysis
pc <- prcomp(ex)
pdf("Results/PC.pdf")
plot(pc)
plot(pc$x[,1:2])
dev.off()

ex.scale <- t(scale(t(ex), scale = FALSE))
pc <- prcomp(ex.scale)
pdf("Results/PC_scaled.pdf")
plot(pc)
plot(pc$x[,1:2])
dev.off()

pcr <- data.frame(pc$r[, 1:3], Group = gr)
pdf("Results/PCA_samples.pdf")
ggplot(pcr, aes(PC1, PC2, color=Group)) + geom_point(size=3) + theme_bw()
pcr <- data.frame(pc$r[, 1:3], Group = gr1)
ggplot(pcr, aes(PC1, PC2, color=Group)) + geom_point(size=3) + theme_bw()
dev.off()

#### TSNE analysis
tsne <- Rtsne(t(ex.scale))
tsne_frame <- data.frame(tsne$Y, Group  = gr)
pdf("Results/TSNE_samples.pdf")
ggplot(tsne_frame, aes(X1, X2, color=Group)) + geom_point(size=3) + theme_bw()
tsne_frame <- data.frame(tsne$Y, Group  = gr1)
ggplot(tsne_frame, aes(X1, X2, color=Group)) + geom_point(size=3) + theme_bw()
dev.off()


#### differential expression analysis
gr <- factor(gr)
gset$description <- gr
design <- model.matrix(~ description + 0, gset)
colnames(design) <- levels(gr)
fit <- lmFit(gset, design)
cont.matrix <- makeContrasts(AML - Normal, levels=design)
fit2 <- contrasts.fit(fit, cont.matrix)
fit2 <- eBayes(fit2, 0.01)
tT <- topTable(fit2, adjust="fdr", sort.by="B", number=Inf)

tT <- subset(tT, select=c("Gene.symbol", "Gene.ID", "adj.P.Val", "logFC"))
write.table(tT, file="Results/AML_Normal.txt", row.names=F, sep="\t", quote = FALSE)


#### gene selection
aml.up <- subset(tT, logFC > 1 & adj.P.Val < 0.05)
# aml.up.genes <- unique(aml.up$Gene.symbol)
# aml.up.genes <- sub("///.*", "", aml.up.genes)
aml.up.genes <- unique(as.character(strsplit2(aml.up$Gene.symbol, "///")))
write.table(aml.up.genes, file="Results/AML_Normal_Up.txt", row.names=F, sep="\t", quote = FALSE, col.names = FALSE)

aml.down <- subset(tT, logFC < -1 & adj.P.Val < 0.05)
# aml.down.genes <- unique(aml.down$Gene.symbol)
# aml.down.genes <- sub("///.*", "", aml.down.genes)
aml.down.genes <- unique(as.character(strsplit2(aml.down$Gene.symbol, "///")))
write.table(aml.down.genes, file="Results/AML_Normal_Down.txt", row.names=F, sep="\t", quote = FALSE, col.names = FALSE)

#### gene set enrichment analysis expression dataset
data1 <- read.delim("Data/GSE48558_series_matrix.txt", comment.char = "!")
data1[, -1] <- log2(1 + data1[, -1])
colnames(data1) <- c("Name", paste0("AML", 1:13), paste0("X", 1:27), "Normal1", paste0("X", 28:30), "Normal2", paste0("X", 31:53), "Normal3", "X54", "Normal4",
                     paste0("X", 55:57), "Normal5", "X58", paste0("Normal", 6:9), "X59", "Normal10", paste0("X", 60:61), paste0("Normal", 11:12), 
                     paste0("X", 62:63), paste0("Normal", 13:14), "X64", "Normal15", "X65", "Normal16", "X66", "Normal17", "X67", "Normal18", "X68", "Normal19",
                     paste0("X", 69:71), "Normal20", paste0("X", 72:74), "Normal21", paste0("X", 75:103), paste0("Normal", 22:28), paste0("AML", 14:15), "Normal29",
                     paste0("AML", 16:18), paste0("Normal", 30:49))
write.table(data1, file="Results/Normal_gsea.txt", row.names=F, sep="\t", quote = FALSE, col.names = TRUE)









