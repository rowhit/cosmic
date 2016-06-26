-- Schema upgrade from 3.0 to 3.0.1;

INSERT IGNORE INTO `cloud`.`configuration` VALUES ('Project Defaults', 'DEFAULT', 'management-server', 'max.project.networks', '20', 'The default maximum number of networks that can be created for a project');


INSERT IGNORE INTO `cloud`.`configuration` VALUES ('Account Defaults', 'DEFAULT', 'management-server', 'max.account.networks', '20', 'The default maximum number of networks that can be created for an account');

INSERT IGNORE INTO `cloud`.`configuration` VALUES ('Account Defaults', 'DEFAULT', 'management-server', 'max.account.networks', '20', 'The default maximum number of networks that can be created for an account');

UPDATE snapshots SET removed=now() WHERE removed IS NULL AND sechost_id IN (SELECT id FROM host WHERE type='SecondaryStorage' AND removed IS NOT NULL);

ALTER TABLE `cloud_usage`.`usage_ip_address` MODIFY COLUMN `is_system` smallint(1) NOT NULL default '0';

ALTER TABLE `cloud_usage`.`account` ADD CONSTRAINT `uc_account__uuid` UNIQUE (`uuid`);

ALTER TABLE `cloud`.`host` ALTER COLUMN `resource_state` SET DEFAULT 'Enabled';

ALTER TABLE `cloud`.`physical_network_service_providers` ADD COLUMN `removed` datetime COMMENT 'date removed if not null';

UPDATE `cloud`.`user` SET PASSWORD=RAND() WHERE id=1;
