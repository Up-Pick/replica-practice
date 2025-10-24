#!/bin/bash

echo "Master-Slave 복제 설정을 시작합니다..."

# Master에서 복제 사용자 생성 및 상태 확인
docker exec -i mysql-master mysql -uroot -proot1234 <<EOF
CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED WITH mysql_native_password BY 'repl1234';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;
FLUSH TABLES WITH READ LOCK;
SHOW MASTER STATUS;
EOF

# Master 상태 저장
MASTER_STATUS=$(docker exec mysql-master mysql -uroot -proot1234 -e "SHOW MASTER STATUS\G")
LOG_FILE=$(echo "$MASTER_STATUS" | grep "File:" | awk '{print $2}')
LOG_POS=$(echo "$MASTER_STATUS" | grep "Position:" | awk '{print $2}')

echo "Master Log File: $LOG_FILE"
echo "Master Log Position: $LOG_POS"

# Slave 설정
docker exec -i mysql-slave mysql -uroot -proot1234 <<EOF
STOP SLAVE;
CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='repl',
  MASTER_PASSWORD='repl1234',
  MASTER_LOG_FILE='$LOG_FILE',
  MASTER_LOG_POS=$LOG_POS;
START SLAVE;
SHOW SLAVE STATUS\G
EOF

# Master 잠금 해제
docker exec mysql-master mysql -uroot -proot1234 -e "UNLOCK TABLES;"

echo "복제 설정이 완료되었습니다!"