# Build war by executing gradle build -x test, then run following scp command
scp -i ./ssh-key-2023-09-27.key /data/prj/abo/web/build/libs/web-0.0.1-SNAPSHOT.jar opc@138.3.254.250:/home/opc/web
