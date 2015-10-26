# Creates data volume container for mysql database which is
# used by AM and BAM to query analytics and to store them.
# Volume /var/lib/mysql is restored from analytics_db_backup.tar
# Backup file was as follows
#    - setup AM / BAM / MYSQL containers
#    - go to AM/admin-dashboard and setup analytics
#	 - when setup is saved, AM/BAM initialized analytics tables to mysql 
#	 - backup database for reuse

FROM ubuntu
MAINTAINER Timo Tapanainen <timo.tapanainen@tieto.com>
ADD analytics_db_backup.tar /
VOLUME /var/lib/mysql
CMD ["true"]