#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <libgen.h>
#include "disk_emu.h"
#include "sfs_api.h"

superBlock superBlock1;
InodeTable myInodeTable;
BitMap myBitMap;
fd fd1[maxOpenedFiles];
DirectoryEntryTable myRootDirectory;

void init_superblock() {

    superBlock1.magic = 0xACBD0005;
    superBlock1.block_size = blockSize;
    superBlock1.fs_size = numberOfBlocks * blockSize;
    superBlock1.inode_table_len = numberINodeBlocks;
    superBlock1.root_dir_inode = 0;
}

int helpGetFreeBlock(){

  printf("withing get free block\n");
  read_blocks(startOfBitMap,totalBitMapBlocks,&myBitMap);
  for(int i = 0; i < numberOfBlocks - dataBlockStart; i++){
    if(myBitMap.blocks[i] == unused){
      myBitMap.blocks[i] = used;
      printf("%d is the free block being returned\n",i);
      write_blocks(startOfBitMap,totalBitMapBlocks,&myBitMap);
      return i + dataBlockStart;
  }
}
printf("There are no more free blocks\n");
return 0;
}

int helpGetFreeFileDescriptor(){

  int ind = -1;
  for(int i = 1; i < maxOpenedFiles; i++){

    if(fd1[i].status == unused){
      fd1[i].status = used;
      ind = i;
      break;
  }
}
return ind;
}


int helpGetBlockPointer(inode_t* inode , int blockNumber){

  int indirectPointer[blockSize/blockPointerSize];
  if(blockNumber < 12){
    if(inode->direct[blockNumber] == 0){
      inode->direct[blockNumber] = helpGetFreeBlock();
      if(inode->direct[blockNumber] == 0)
        return -1;
}
return inode->direct[blockNumber];
}

if(inode->indirectPointer == 0){
    inode->indirectPointer = helpGetFreeBlock();
    if(inode-> indirectPointer == 0) return -1;
  memset(indirectPointer, 0, blockSize);
  write_blocks(inode->indirectPointer, 1, (void *) indirectPointer);
}

read_blocks(inode->indirectPointer, 1, (void *) indirectPointer);
if(indirectPointer[blockNumber - 12] == 0){
    indirectPointer[blockNumber - 12] = helpGetFreeBlock();
    if(indirectPointer[blockNumber - 12] == 0)
      return -1;

  write_blocks(inode->indirectPointer, 1, (void *)indirectPointer);
}
return indirectPointer[blockNumber - 12];
}

//release all block pointers from inode
void helpReleaseBlockPointer(inode_t inode){

  int indirectPointer[blockSize/blockPointerSize];

  read_blocks(startOfBitMap,totalBitMapBlocks,(void *)&myBitMap);
  for(int i = 0; i < 12; i++){
    if(inode.direct[i] == 0){
      write_blocks(startOfBitMap,totalBitMapBlocks,(void *)&myBitMap);
      return;
  }
  myBitMap.blocks[inode.direct[i] - startOfBitMap] = unused;
}

  //for indirectPointer pointers
read_blocks(inode.indirectPointer, 1, (void *)indirectPointer);
for(int i = 0; i < blockSize/blockPointerSize; i++){
    if(indirectPointer[i] == 0){
      write_blocks(startOfBitMap,totalBitMapBlocks,(void *)&myBitMap);
      return;
  }
  myBitMap.blocks[indirectPointer[i] - startOfBitMap] = unused;
}
}

//get the next free node from inode index
int helpGetNextFreeINodeIndex(){

  int ind = -1;
  read_blocks(startOfBitMap,totalBitMapBlocks,&myBitMap);
  for(int i = 1; i< numberINodes; i++){

    //printf("YOUR INODE INDEX AT %d: %d\n", i, myBitMap.inodeIndexes[i]);
    if(myBitMap.inodeIndexes[i] == unused){
      //printf("Index %d is in the BITMAP that is unused\n", i);
       myBitMap.inodeIndexes[i] = used;
       write_blocks(startOfBitMap,totalBitMapBlocks,&myBitMap);

       ind = i;
       break;
   }
}

return ind;
}

int helpGetFreeDirectorySpace(){

  for(int i = 0; i< numberINodes-1; i++){
    if(myRootDirectory.entries[i].status == unused){
      myRootDirectory.entries[i].status = used;
      return i;

  }
}
return -1;
}

void helpWriteNewNode(int index, inode_t inode){
  //read the inode table

  read_blocks(iNodeBlockStart, numberINodeBlocks, &myInodeTable);
  myInodeTable.table[index] = inode;
  write_blocks(iNodeBlockStart, numberINodeBlocks, &myInodeTable);
}

inode_t helpGetINode(int index){
  inode_t inode;

  read_blocks(iNodeBlockStart, numberINodeBlocks, &myInodeTable);

  inode = myInodeTable.table[index];
  return inode;
}

int helpGetINodeByName( char* name){ //int helpGetINodeByName( char* name){ 

  for(int i = 0; i < numberINodes; i++){
    if(myRootDirectory.entries[i].status == used){
      if(strcmp(myRootDirectory.entries[i].fileName, name) == 0){
        return myRootDirectory.entries[i].inodeNum;
    }
}
}
return -1;
}

void helpRemoveINodeByName(char* name){

  for(int i = 0; i <numberINodes; i++){
    if(myRootDirectory.entries[i].status == used){
      if(strcmp(myRootDirectory.entries[i].fileName, name) == 0){
        //free directory
        myRootDirectory.entries[i].status = unused;

        write_blocks(directoryBlockStart, numberOfDirectoryBlocks,&myRootDirectory);

        int inodeIndex = myRootDirectory.entries[i].inodeNum;
        //free blocks
        read_blocks(iNodeBlockStart, numberINodeBlocks, &myInodeTable);
        helpReleaseBlockPointer(myInodeTable.table[inodeIndex]);
        //free bitmap space

        read_blocks(startOfBitMap, totalBitMapBlocks, &myBitMap);
        myBitMap.inodeIndexes[inodeIndex] = unused;
        write_blocks(startOfBitMap, totalBitMapBlocks, &myBitMap);
    }
}
}
}

void mksfs(int fresh){

    for (int i = 0; i <maxOpenedFiles; i++){
      fd1[i].status = unused;
  }
  if (fresh) {

    printf("Making simple file system...\n");
        // create super block
    init_superblock();

    init_fresh_disk(disk, blockSize, numberOfBlocks);


         write_blocks(0, 1, &superBlock1);
         read_blocks(0,1, &superBlock1);

         int j;
        myBitMap.inodeIndexes[0] = used; // taken by root directory inode
      for(j = 0; j < numberINodes; j++){
          myBitMap.inodeIndexes[j] = unused; //set the rest of the inode indexes to free
      }
      for(int i = 0; i < numberOfBlocks; i++){
          myBitMap.blocks[i] = unused;
      }


      write_blocks(startOfBitMap , totalBitMapBlocks, &myBitMap);

      fd1[0].status = used;
      fd1[0].writeptr = 0;
      fd1[0].readptr = 0;
      fd1[0].inode = 0;
      myInodeTable.table[0].size = 0;

      for(int i = 0; i < numberINodes; i++){
          myRootDirectory.entries[i].status = unused;
      }
      write_blocks(iNodeBlockStart, numberINodeBlocks, &myInodeTable);


      write_blocks(directoryBlockStart, numberOfDirectoryBlocks, &myRootDirectory);



        // write free block list
  } else {
    printf("reopening file system\n");
    init_disk(disk, blockSize,numberOfBlocks);
    read_blocks(0, 1, &superBlock1);
    read_blocks(directoryBlockStart, numberOfDirectoryBlocks, &myRootDirectory);

}
}

int sfs_get_next_file_name(char *fname){

  for(int i = 0; i < numberINodes; i++){
    if(myRootDirectory.entries[i].status == used){
      strcpy(fname, myRootDirectory.entries[i].fileName);
  }
}

return 0;
}

int sfs_get_file_size(char* path){ 

    //Implement sfs_getfilesize here
  inode_t inode;
  int inodeIndex = helpGetINodeByName(path);
  if(inodeIndex == -1){
    return -1;
  }

  inode = helpGetINode(inodeIndex);
  int size = inode.size;

  return size;
}

int sfs_fopen(char *name){

  printf("In sfs_fopen\n" );
  int inodeIndex = helpGetINodeByName(name);


  inode_t inode;
  //init new inode values
  inode.size = 0;
  for(int i = 0; i< 12; i++){
    inode.direct[i] = 0;
}
inode.indirectPointer = 0;

for(int i = 1; i < maxOpenedFiles; i++){
    if(fd1[i].status == used && fd1[i].inode == inodeIndex) return i;
}

int fd = helpGetFreeFileDescriptor();
if(fd == -1) return -1;

int directoryIndex = helpGetFreeDirectorySpace();



  if(inodeIndex == -1){ //new file!

    inodeIndex = helpGetNextFreeINodeIndex();

    myRootDirectory.entries[directoryIndex].inodeNum = inodeIndex;
    strcpy(myRootDirectory.entries[directoryIndex].fileName, name);
    myRootDirectory.entries[directoryIndex].status = used;
    write_blocks(directoryBlockStart, numberOfDirectoryBlocks, &myRootDirectory);
    helpWriteNewNode(inodeIndex,inode);

}
else{
    inode =helpGetINode( helpGetINodeByName(name));
}
printf("open success\n" );
myInodeTable.table[inodeIndex] = inode;
fd1[fd].status = used;
fd1[fd].inode = inodeIndex;
fd1[fd].writeptr = inode.size;
fd1[fd].readptr = 0;
return fd;
}

int sfs_fclose(int fileID){

    if(fileID >= 0){ //unused is defined as 1 in .h
          if (fd1[fileID].status == unused){
            printf("%i This file was never created \n", fileID);
            return -1;
          } 
          else{
            helpWriteNewNode(fd1[fileID].inode, myInodeTable.table[fd1[fileID].inode]);
            printf("%i A file was successfully closed \n", fileID);
            fd1[fileID].status = unused; //
           // helpWriteNewNode(fd1[fileID].inode, myInodeTable.table[fd1[fileID].inode]); //moves to next note
            return 0;
          }
        }
    else{
        printf("This file does not exist!!!! \n");
        return -1;
    }
    return -1;
}


int sfs_frseek(int fileID, int loc){
//does not work right
    if(fd1[fileID].status == unused) {
      return -1;
    }
    else{
    if(loc > myInodeTable.table[fd1[fileID].inode].size)
      loc =  myInodeTable.table[fd1[fileID].inode].size;
      fd1[fileID].readptr = loc;
      return 0;
    }
  }

int sfs_fwseek(int fileID, int loc){
//does not work right

    if(fd1[fileID].status <= unused){
      printf("%i Don't Try something negative thx\n", fileID);
      return -1;
    } 
    if(loc >= myInodeTable.table[fd1[fileID].inode].size){
      loc =  myInodeTable.table[fd1[fileID].inode].size;
      fd1[fileID].writeptr = loc;
      return 0;
    }
    return 0;
}

int sfs_fwrite(int fileID,  char *buf, int length){ 

  inode_t* inode;

  int writeptr = fd1[fileID].writeptr;
  int blockNumber = writeptr/blockSize;
  int byteNumber = writeptr%blockSize;

  int blockPointer, lastFullBlock, lastBlockByte;
  char block[blockSize];

  if(fd1[fileID].writeptr + length > maxFileSize) return -1;
  inode = &myInodeTable.table[fd1[fileID].inode];

  if(byteNumber + length <= blockSize){
    blockPointer = helpGetBlockPointer(inode, blockNumber);
    if(blockPointer == -1) return -1;

    read_blocks(blockPointer, 1, block);
    memcpy(&block[byteNumber], buf, length);
    write_blocks(blockPointer, 1, block);

    if(writeptr + length > inode->size) inode->size = writeptr + length;
    //Write to inode table here
    fd1[fileID].writeptr += length;
    return length;
}

lastFullBlock = (byteNumber + length)/blockSize + blockNumber;
lastBlockByte = (byteNumber + length) % blockSize;

blockPointer = helpGetBlockPointer(inode, blockNumber);

if(blockPointer == -1) return -1;
read_blocks(blockPointer, 1, block);
memcpy(&block[byteNumber], buf, blockSize - byteNumber);
write_blocks(blockPointer, 1, block);

buf += blockSize - byteNumber;
blockNumber ++;

while(blockNumber < lastFullBlock){

    blockPointer = helpGetBlockPointer(inode, blockNumber);
    if(blockPointer == -1) return -1;
    memcpy(block, buf, blockSize );
    write_blocks(blockPointer, 1, block);

    buf += blockSize - byteNumber;
    blockNumber ++;
}

blockPointer = helpGetBlockPointer(inode, blockNumber);

if(blockPointer == -1) return -1;

read_blocks(blockPointer, 1, block);
memcpy(block, buf, lastBlockByte );
write_blocks(blockPointer, 1, block);

if(writeptr + length > inode->size) inode->size = writeptr + length;

write_blocks(iNodeBlockStart, numberINodeBlocks,&myInodeTable);
fd1[fileID].writeptr += length;
return length;
return 0;
}


int sfs_fread(int fileID, char *buf, int length){

  if(fd1[fileID].status == unused) {
    return -1;
  }


  inode_t* inode;
  int readptr = fd1[fileID].readptr;
  int blockNumber = readptr/ blockSize;
  int blockPointer, lastFullBlock, lastBlockByte;
  char block[blockSize];

  int byteNumber = readptr% blockSize; //IMPORTANT
//********************************************************************************
/*
  int adjustedLength = 0;
    if(fd1[fileID].writeptr < length){
    adjustedLength = fd1[fileID].writeptr;
  } else {
    adjustedLength = length;
  }

  int rPtr = fd1[fileID].readptr;
  int rPtrBlock = (rPtr - rPtr%blockSize)/blockSize;
  int currentFatEntry = myBitMap.blocks[currentFatEntry];

  int i;
  for(i = 0; i < rPtrBlock; i++){
    currentFatEntry = myBitMap.blocks[currentFatEntry];
  }

  char tempBuffer[blockSize];
  read_blocks(myBitMap.blocks[currentFatEntry], 1,tempBuffer);

*/
  //********************************************************************************8888

  inode = &myInodeTable.table[fd1[fileID].inode];

  if(readptr + length > inode->size){

  length = inode->size - readptr;

  }

  if(byteNumber + length <= blockSize){
    blockPointer = helpGetBlockPointer(inode, blockNumber);
    read_blocks(blockPointer, 1, block);
    memcpy(buf, &block[byteNumber],length);
    fd1[fileID].readptr = readptr + length;
    return length;
  }

  lastFullBlock = (byteNumber + length)/blockSize + blockNumber;
  lastBlockByte = (byteNumber + length) % blockSize;

  blockPointer = helpGetBlockPointer(inode, blockNumber);
  read_blocks(blockPointer, 1, block);
  memcpy(buf, &block[byteNumber],blockSize - byteNumber);
  buf += blockSize - byteNumber;
  blockNumber++;

  while(blockNumber < lastFullBlock){
      blockPointer = helpGetBlockPointer(inode, blockNumber);
      read_blocks(blockPointer,1,block);
      memcpy(buf,block,blockSize);
      buf += blockSize;
      blockNumber++;
  }

    //The last block
  blockPointer = helpGetBlockPointer(inode, blockNumber);
  read_blocks(blockPointer, 1, block);
  memcpy(buf,block,lastBlockByte);

  fd1[fileID].readptr += length;
  return length;



}

int sfs_remove(char *file){

  int inodeIndex = helpGetINodeByName(file);
  if(inodeIndex == -1) return -1;
  helpRemoveINodeByName(file);

    //Implement sfs_remove here
  return 0;
}
